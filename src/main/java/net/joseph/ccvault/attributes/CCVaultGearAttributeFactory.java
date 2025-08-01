package net.joseph.ccvault.attributes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.MultipleGearAttributeRollOutputEntry;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierAffixTagGroup;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierConfigRange;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierOutcome;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierTier;
import iskallia.vault.effect.PoisonOverrideEffect;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixType;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.DoubleAttributeGenerator;
import iskallia.vault.gear.attribute.config.FloatAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator.Range;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceListGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceListGearAttribute.Config;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute.EffectCloud;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.reader.IntegerModifierReader;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModGearModifications;
import net.joseph.ccvault.interfaces.ILuaTable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CCVaultGearAttributeFactory {
    public static CCVaultGearAttribute parse(ItemStack stack, VaultGearModifier<?> modifier) {
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
                    return new RangedAttribute<Integer>(name, (Integer) value, range.get().getMin(),
                            range.get().getMax());
                }
                ;
            }

        }
        // Configs are generally split by level and by roll tiers, so we query by level
        VaultGearTierConfig.ModifierConfigRange modifierConfig = getModifierConfigForLevel(stack, modifier, data.getItemLevel());
        if (value instanceof Integer) {
             //Since allTierConfigs "Nicely" returns object for we need to cast it all to a type we know
             // Config Ranges are the ranges the values can roll, within every Tier
             // so we get a list of all ranges, for all the possible tiers at this level
            List<IntegerAttributeGenerator.Range> ranges = castList(modifierConfig.allTierConfigs());
            IntegerAttributeGenerator gen = new IntegerAttributeGenerator();
            //Then we use the generator funtions to get minimum and the highest value in the roll
            return new RangedAttribute<Integer>(name, (Integer) value, gen.getMinimumValue(ranges).get(),
                    gen.getMaximumValue(ranges).get());

        } else if (value instanceof Float) {

            List<FloatAttributeGenerator.Range> ranges = castList(modifierConfig.allTierConfigs());
            FloatAttributeGenerator gen = new FloatAttributeGenerator();
            return new RangedAttribute<Float>(name, (Float) value, gen.getMinimumValue(ranges).get(),
                    gen.getMaximumValue(ranges).get());
        } else if (value instanceof Double) {
            List<DoubleAttributeGenerator.Range> ranges = castList(modifierConfig.allTierConfigs());
            DoubleAttributeGenerator gen = new DoubleAttributeGenerator();
            Double min = gen.getMinimumValue(ranges).get();
            Double max = gen.getMaximumValue(ranges).get();
            return new RangedAttribute<Double>(name, (Double) value, min, max);

        } else if (value instanceof Boolean) {
            return new CCVaultGearAttribute(name);
        } else if (value instanceof ManaPerLootAttribute) {
            List<ManaPerLootAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
            ManaPerLootAttribute.Generator gen = ManaPerLootAttribute.generator();
            return new CCManaPerLootAttribute("Manabloom", (ManaPerLootAttribute) value,
                    gen.getMinimumValue(ranges).get(),
                    gen.getMaximumValue(ranges).get());
        } else if (value instanceof EffectAvoidanceListGearAttribute) {
            EffectAvoidanceListGearAttribute v = (EffectAvoidanceListGearAttribute) value;
            List<EffectAvoidanceListGearAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
            ConfigurableAttributeGenerator<EffectAvoidanceListGearAttribute, EffectAvoidanceListGearAttribute.Config> gen = EffectAvoidanceListGearAttribute
                    .generator();
            return new RangedAttribute<Float>("Effect Avoidance", v.getChance(),
                    gen.getMinimumValue(ranges).get().getChance(),
                    gen.getMaximumValue(ranges).get().getChance());
        } else if (value instanceof EffectAvoidanceGearAttribute) {
            EffectAvoidanceGearAttribute v = (EffectAvoidanceGearAttribute) value;
            List<EffectAvoidanceGearAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
            ConfigurableAttributeGenerator<EffectAvoidanceGearAttribute, EffectAvoidanceGearAttribute.Config> gen = EffectAvoidanceGearAttribute
                    .generator();
            return new RangedAttribute<Float>("Effect Avoidance", v.getChance(),
                    gen.getMinimumValue(ranges).get().getChance(),
                    gen.getMaximumValue(ranges).get().getChance());
        } else if (value instanceof AbilityLevelAttribute) {
            AbilityLevelAttribute v = (AbilityLevelAttribute) value;
            List<AbilityLevelAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
            ConfigurableAttributeGenerator<AbilityLevelAttribute, AbilityLevelAttribute.Config> gen = AbilityLevelAttribute
                    .generator();
            return new CCAbilityLevelAttribute(v.getAbility(), v.getLevelChange(),
                    gen.getMinimumValue(ranges).get().getLevelChange(),
                    gen.getMaximumValue(ranges).get().getLevelChange());
        } else if (value instanceof EffectCloudAttribute) {
            EffectCloudAttribute v = (EffectCloudAttribute) value;
            VaultGearModifierReader<EffectCloudAttribute> reader = EffectCloudAttribute.reader(false);
            return new ValueAttribute<String>("Cloud",
                    reader.getValueDisplay(v).getString());
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("modifier", modifier.toString());
            map.put("value", value.toString());
            map.put("configRange", modifierConfig.toString());
            return new DebugAttribute(map);
        }
    }


   
    private static <T> List<T> castList(List<Object> list) {
        return list.stream()
                .map(o -> (T) o).collect(Collectors.toList());
    }

    private static VaultGearTierConfig.ModifierConfigRange getModifierConfigForLevel(ItemStack stack,
            VaultGearModifier<?> modifier, int level) {
        VaultGearTierConfig.ModifierConfigRange configRange = (VaultGearTierConfig.ModifierConfigRange) VaultGearTierConfig
                .getConfig(stack).map((tierCfg) -> {
                    return tierCfg.getTierConfigRange(modifier, level);
                }).orElse(ModifierConfigRange.empty());
        return configRange;
    }

    public static CCVaultGearAttribute parse(ItemStack stack, VaultGearAttributeInstance<?> instance,
            VaultGearData data) {
        if (instance.getAttribute().equals(ModGearAttributes.CRAFTING_POTENTIAL)) {
            return new ValueAttribute<Integer>("Crafting Potential", (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
            return new ValueAttribute<Integer>("Max Crafting Potential", (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.GEAR_MODEL)) {
            ResourceLocation loc = (ResourceLocation) instance.getValue();
            var model = ModDynamicModels.REGISTRIES.getModelByResourceLocation(loc);
            if (model.isPresent()) {
                return new ValueAttribute<String>("Model", model.get().getDisplayName());
            }
            return new ValueAttribute<String>("Model", null);
        } else if (instance.getAttribute().equals(ModGearAttributes.PREFIXES)) {
            return new ValueAttribute<Integer>("Prefixes", (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.SUFFIXES)) {
            return new ValueAttribute<Integer>("Suffixes", (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.DURABILITY)) {
            var t = VaultGearTierConfig.getConfig(stack).get().getModifierGroup(ModifierAffixTagGroup.BASE_ATTRIBUTES)
                    .stream().filter(a -> a.getAttribute().toString().equals("the_vault:durability"))
                    .collect(Collectors.toList()).get(0).getModifiersForLevel(data.getItemLevel()).stream()
                    .map(o -> o.getModifierConfiguration()).collect(Collectors.toList());
            return new RangedAttribute<Integer>("Durabilty", (Integer) instance.getValue(),
                    (Integer) instance.getAttribute().getGenerator().getMinimumValue(t).get(),
                    (Integer) instance.getAttribute().getGenerator().getMaximumValue(t).get());
        }

        if (instance.getValue() instanceof Boolean) {
            return new CCVaultGearAttribute(instance.getAttribute().getReader().getModifierName());
        } else if (instance.getValue() instanceof Integer) {
            return new ValueAttribute<Integer>(instance.getAttribute().getReader().getModifierName(),
                    (Integer) instance.getValue());
        } else if (instance.getValue() instanceof Float) {
            return new ValueAttribute<Float>(instance.getAttribute().getReader().getModifierName(),
                    (Float) instance.getValue());
        } else if (instance.getValue() instanceof Double) {
            return new ValueAttribute<Double>(instance.getAttribute().getReader().getModifierName(),
                    (Double) instance.getValue());
        }
        return new DebugAttribute(instance.toString());
    }
}
