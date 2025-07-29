package net.joseph.ccvault.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierConfigRange;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.DoubleAttributeGenerator;
import iskallia.vault.gear.attribute.config.FloatAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class AffixAttributeFactory {
    public static AffixAttribute parse(ItemStack stack, VaultGearModifier<?> modifier) {
        VaultGearData data = VaultGearData.read(stack);
        String name = modifier.getAttribute().getReader().getModifierName();
        Object value = modifier.getValue();

        // This is ugly, but Jewel size is coded differently from other Modifiers
        // Attempting to get the config the normal way to get the range causes a
        // NoSuchElementException
        // So just hard coding the logic found in JewelItem#onIdentify and adapting it
        // feels correct
        // However if ignoreJewelSize is set we can follow the normal rout for integer
        // modifires
        // ( ͡° ͜ʖ ͡°)
        if (name.equals("Size")) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("ignoreJewelSize") || !tag.getBoolean("ignoreJewelSize")) {
                Optional<IntRangeEntry> range = ModConfigs.JEWEL_SIZE.getSize(data.getRarity());
                if (range.isPresent()) {
                    return new IntegerAffixAttribute(name, (Integer) value, range.get().getMin(), range.get().getMax());
                }
                ;
            }

        }
        if (value instanceof Integer) {
            VaultGearTierConfig.ModifierConfigRange configRange = getConfigRange(stack, modifier, data);
            List<IntegerAttributeGenerator.Range> ranges = configRange.allTierConfigs().stream()
                    .map(o -> (IntegerAttributeGenerator.Range) o).collect(Collectors.toList());
            IntegerAttributeGenerator gen = new IntegerAttributeGenerator();
            return new IntegerAffixAttribute(name, (Integer) value, gen.getMinimumValue(ranges).get(),
                    gen.getMaximumValue(ranges).get());

        } else if (value instanceof Float) {
            VaultGearTierConfig.ModifierConfigRange configRange = getConfigRange(stack, modifier, data);
            List<FloatAttributeGenerator.Range> ranges = configRange.allTierConfigs().stream()
                    .map(o -> (FloatAttributeGenerator.Range) o).collect(Collectors.toList());
            FloatAttributeGenerator gen = new FloatAttributeGenerator();
            return new FloatAffixAttribute(name, (Float) value, gen.getMinimumValue(ranges).get(),
                    gen.getMaximumValue(ranges).get());
        } else if (value instanceof Double) {
            VaultGearTierConfig.ModifierConfigRange configRange = getConfigRange(stack, modifier, data);

            List<DoubleAttributeGenerator.Range> ranges = configRange.allTierConfigs().stream()
                    .map(o -> (DoubleAttributeGenerator.Range) o).collect(Collectors.toList());
            DoubleAttributeGenerator gen = new DoubleAttributeGenerator();
            Double min = gen.getMinimumValue(ranges).get();
            Double max = gen.getMaximumValue(ranges).get();
            return new DoubleAffixAttribute(name, (Double) value, min, max);

        } else if (value instanceof Boolean) {
            return new AffixAttribute(name);
        } else if (value instanceof ManaPerLootAttribute) {
            VaultGearTierConfig.ModifierConfigRange configRange = getConfigRange(stack, modifier, data);
            List<ManaPerLootAttribute.Config> ranges = configRange.allTierConfigs().stream()
                    .map(o -> (ManaPerLootAttribute.Config) o).collect(Collectors.toList());
            ManaPerLootAttribute.Generator gen = ManaPerLootAttribute.generator();
            return new ManaPerLootAffixAttribute("Manabloom", (ManaPerLootAttribute) value,
                    gen.getMinimumValue(ranges).get(),
                    gen.getMaximumValue(ranges).get());
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("modifier", modifier.toString());
            map.put("value", value.toString());
            VaultGearTierConfig.ModifierConfigRange configRange = getConfigRange(stack, modifier, data);
            map.put("configRange", configRange.toString());
            return new DebugAffixAttribute(map);
        }
    }

    private static VaultGearTierConfig.ModifierConfigRange getConfigRange(ItemStack stack,
            VaultGearModifier<?> modifier, VaultGearData data) {
        VaultGearTierConfig.ModifierConfigRange configRange = (VaultGearTierConfig.ModifierConfigRange) VaultGearTierConfig
                .getConfig(stack).map((tierCfg) -> {
                    return tierCfg.getTierConfigRange(modifier, data.getItemLevel());
                }).orElse(ModifierConfigRange.empty());
        return configRange;
    }
}
