package net.joseph.ccvault.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierConfigRange;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.ability.AbilityAreaOfEffectPercentAttribute;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute.SpecialAbilityTierConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.FloatValue;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.custom.ability.AbilityTriggerOnDamageAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceListGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectTrialAttribute;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CCVaultGearAttributeFactory {
    public static CCVaultGearAttribute parse(ItemStack stack, VaultGearModifier<?> modifier) {
        VaultGearData data = VaultGearData.read(stack);
        String name = modifier.getAttribute().getReader().getModifierName();
        Object value = modifier.getValue();

        // Tier -1 is used to define things that dont roll a tier, like tools, gem
        // sizes, etc
        // why of all places getRolledTier doesnt return a nullable value instead, is
        // out of my non giga brain developer brain
        if (modifier.getRolledTier() == -1) {
            return parseDeterministicModifier(modifier);
        }
        // while we are at it we can also get the tier, since tiers are 0 indexed in
        // code but 1 indexed in the client,
        // we just correct for that so it matches what players can read with their
        // eyeballs
        int tier = modifier.getRolledTier() + 1;

        VaultGearTierConfig.ModifierConfigRange modifierConfig = getModifierConfigForLevel(stack, modifier,
                data.getItemLevel());
        List<Object> allTierConfigs = modifierConfig.allTierConfigs();
        try {
            if (value instanceof Integer) {
                return new RangedValueAttribute<Integer>(name,
                        modifier.getCategories(),
                        (Integer) value,
                        tier,
                        ((IntegerAttributeGenerator.Range) modifierConfig.minAvailableConfig()).min,
                        ((IntegerAttributeGenerator.Range) modifierConfig.maxAvailableConfig()).max);
            } else if (value instanceof Float) {
                Float min = (Float) FieldUtils.readField(modifierConfig.minAvailableConfig(), "min", true);
                Float max = (Float) FieldUtils.readField(modifierConfig.maxAvailableConfig(), "max", true);
                return new RangedValueAttribute<Float>(name,
                        modifier.getCategories(),
                        (Float) value,
                        tier,
                        min,
                        max);
            } else if (value instanceof Double) {

                Double min = (Double) FieldUtils.readField(modifierConfig.minAvailableConfig(), "min", true);
                Double max = (Double) FieldUtils.readField(modifierConfig.maxAvailableConfig(), "max", true);
                return new RangedValueAttribute<Double>(name,
                        modifier.getCategories(),
                        (Double) value,
                        tier,
                        min,
                        max);

            } else if (value instanceof Boolean) {
                return new CCVaultGearAttribute(name, modifier.getCategories());
            } else if (value instanceof ManaPerLootAttribute) {
                Pair<Integer, Float> v = new Pair<>(((ManaPerLootAttribute) value).getManaGenerated(),
                        ((ManaPerLootAttribute) value).getManaGenerationChance());
                if (allTierConfigs != null) {
                    ManaPerLootAttribute.Config min_cfg = ((ManaPerLootAttribute.Config) modifierConfig
                            .minAvailableConfig());
                    ManaPerLootAttribute.Config max_cfg = ((ManaPerLootAttribute.Config) modifierConfig
                            .maxAvailableConfig());

                    Pair<Integer, Float> min = new Pair<>(min_cfg.getManaGenerated().getMin(),
                            min_cfg.getManaGenerationChance().getMin());
                    Pair<Integer, Float> max = new Pair<>(max_cfg.getManaGenerated().getMax(),
                            min_cfg.getManaGenerationChance().getMax());
                    return new RangedManaPerLootAttribute(modifier.getCategories(),
                            v,
                            tier,
                            min,
                            max);
                }
                return new CCManaPerLootAttribute(modifier.getCategories(), v, tier);
            } else if (value instanceof EffectAvoidanceListGearAttribute) {
                EffectAvoidanceListGearAttribute v = (EffectAvoidanceListGearAttribute) value;
                List<EffectAvoidanceListGearAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
                ConfigurableAttributeGenerator<EffectAvoidanceListGearAttribute, EffectAvoidanceListGearAttribute.Config> gen = EffectAvoidanceListGearAttribute
                        .generator();
                return new RangedValueAttribute<Float>("Effect Avoidance",
                        modifier.getCategories(),
                        v.getChance(),
                        tier,
                        gen.getMinimumValue(ranges).get().getChance(),
                        gen.getMaximumValue(ranges).get().getChance());
            } else if (value instanceof EffectAvoidanceGearAttribute) {
                EffectAvoidanceGearAttribute v = (EffectAvoidanceGearAttribute) value;
                List<EffectAvoidanceGearAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
                ConfigurableAttributeGenerator<EffectAvoidanceGearAttribute, EffectAvoidanceGearAttribute.Config> gen = EffectAvoidanceGearAttribute
                        .generator();
                return new RangedValueAttribute<Float>("Effect Avoidance",
                        modifier.getCategories(),
                        v.getChance(),
                        tier,
                        gen.getMinimumValue(ranges).get().getChance(),
                        gen.getMaximumValue(ranges).get().getChance());
            } else if (value instanceof AbilityLevelAttribute) {
                AbilityLevelAttribute v = (AbilityLevelAttribute) value;
                List<AbilityLevelAttribute.Config> ranges = castList(modifierConfig.allTierConfigs());
                ConfigurableAttributeGenerator<AbilityLevelAttribute, AbilityLevelAttribute.Config> gen = AbilityLevelAttribute
                        .generator();
                return new CCAbilityLevelAttribute(v.getAbility(), tier, v.getLevelChange(),
                        gen.getMinimumValue(ranges).get().getLevelChange(),
                        gen.getMaximumValue(ranges).get().getLevelChange(), modifier.getCategories());
            } else if (value instanceof EffectCloudAttribute) {
                EffectCloudAttribute v = (EffectCloudAttribute) value;
                VaultGearModifierReader<EffectCloudAttribute> reader = EffectCloudAttribute.reader(false);
                return new TieredValueAttribute<String>("Cloud",
                        modifier.getCategories(),
                        reader.getValueDisplay(v).getString(),
                        tier);
            } else if (value instanceof SpecialAbilityGearAttribute) {
                String ability = ((SpecialAbilityGearAttribute) value).getAbilityKey();
                String modification = ((SpecialAbilityGearAttribute) value).getModification().getKey().toString();
                var v = ((SpecialAbilityGearAttribute) value).getValue();
                if (v instanceof IntValue) {
                    try {

                        Integer min = (Integer) FieldUtils.readField(
                                ((SpecialAbilityTierConfig) modifierConfig.minAvailableConfig()).getConfig(), "min",
                                true);
                        Integer max = (Integer) FieldUtils.readField(
                                ((SpecialAbilityTierConfig) modifierConfig.minAvailableConfig()).getConfig(), "max",
                                true);
                        return new SpecialAbilityAttribute<Integer>(ability, modification, tier,
                                ((IntValue) v).getValue(),
                                min, max, modifier.getCategories());
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (v instanceof FloatValue) {
                    try {
                        Float min = (Float) FieldUtils.readField(
                                ((SpecialAbilityTierConfig) modifierConfig.minAvailableConfig()).getConfig(), "min",
                                true);
                        Float max = (Float) FieldUtils.readField(
                                ((SpecialAbilityTierConfig) modifierConfig.minAvailableConfig()).getConfig(), "max",
                                true);
                        return new SpecialAbilityAttribute<Float>(ability, modification, tier,
                                ((FloatValue) v).getValue(),
                                min, max, modifier.getCategories());
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } else if (value instanceof AbilityTriggerOnDamageAttribute) {
                AbilityTriggerOnDamageAttribute v = (AbilityTriggerOnDamageAttribute) value;
                Pair<Integer, Float> current = new Pair<Integer, Float>(v.getLevel(), v.getChance());
                AbilityTriggerOnDamageAttribute.Config min = (AbilityTriggerOnDamageAttribute.Config) modifierConfig
                        .minAvailableConfig();
                AbilityTriggerOnDamageAttribute.Config max = (AbilityTriggerOnDamageAttribute.Config) modifierConfig
                        .maxAvailableConfig();
                return new CCAbilityTriggerOnDamageAttribute(modifier.getCategories(),
                        v.getAbilityId(),
                        current,
                        tier, new Pair<Integer, Float>(min.getLevel().getMin(), min.getChance().getMin()),
                        new Pair<Integer, Float>(max.getLevel().getMax(), max.getChance().getMax()));
            } else if (value instanceof AbilityAreaOfEffectPercentAttribute) {
                AbilityAreaOfEffectPercentAttribute v = (AbilityAreaOfEffectPercentAttribute) value;
                AbilityAreaOfEffectPercentAttribute.Config min = (AbilityAreaOfEffectPercentAttribute.Config) modifierConfig
                        .minAvailableConfig();
                AbilityAreaOfEffectPercentAttribute.Config max = (AbilityAreaOfEffectPercentAttribute.Config) modifierConfig
                        .maxAvailableConfig();
                return new CCAbilityAOEAttribute(modifier.getCategories(),
                        v.getAbilityKey(),
                        tier,
                        v.getAmount(),
                        min.getMin(),
                        max.generateMaximumValue());
            } else if (value instanceof AbilityCooldownPercentAttribute) {
                AbilityCooldownPercentAttribute v = (AbilityCooldownPercentAttribute) value;
                AbilityCooldownPercentAttribute.Config min = (AbilityCooldownPercentAttribute.Config) modifierConfig
                        .minAvailableConfig();
                AbilityCooldownPercentAttribute.Config max = (AbilityCooldownPercentAttribute.Config) modifierConfig
                        .maxAvailableConfig();
                return new CCAbilityCDAttribute(modifier.getCategories(),
                        v.getAbilityKey(),
                        tier,
                        v.getAmount(),
                        min.getMin(),
                        max.generateMaximumValue());
            } else if (value instanceof EffectGearAttribute) {
                EffectGearAttribute v = (EffectGearAttribute) value;

                Integer min = (Integer) FieldUtils.readField(modifierConfig.minAvailableConfig(), "amplifier", true);
                Integer max = (Integer) FieldUtils.readField(modifierConfig.maxAvailableConfig(), "amplifier", true);
                return new CCEffectAttribute(v.getEffect().getDisplayName().getString(),
                        modifier.getCategories(),
                        tier,
                        v.getAmplifier(),
                        min,
                        max);
            } else if (value instanceof EffectTrialAttribute) {
                EffectTrialAttribute v = (EffectTrialAttribute) value;
                Integer min = ((EffectTrialAttribute.Config) modifierConfig
                        .minAvailableConfig()).getDurationTicks().getMin();
                Integer max = ((EffectTrialAttribute.Config) modifierConfig
                        .maxAvailableConfig()).getDurationTicks().getMax();
                return new TrailAttribute(v.getEffectId().toString(), modifier.getCategories(), v.getDurationTicks(),
                        tier, min, max);

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("modifier", modifier.toString());
        map.put("value", value.toString());
        map.put("configRange", modifierConfig.toString());
        return new DebugAttribute(map);

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
            return new ValueAttribute<Integer>("Crafting Potential", new AffixCategorySet(),
                    (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
            return new ValueAttribute<Integer>("Max Crafting Potential", new AffixCategorySet(),
                    (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.GEAR_MODEL)) {
            ResourceLocation loc = (ResourceLocation) instance.getValue();
            var model = ModDynamicModels.REGISTRIES.getModelByResourceLocation(loc);
            if (model.isPresent()) {
                return new ValueAttribute<String>("Model", new AffixCategorySet(), model.get().getDisplayName());
            }
            return new ValueAttribute<String>("Model", new AffixCategorySet(), null);
        } else if (instance.getAttribute().equals(ModGearAttributes.PREFIXES)) {
            return new ValueAttribute<Integer>("Prefixes", new AffixCategorySet(), (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.SUFFIXES)) {
            return new ValueAttribute<Integer>("Suffixes", new AffixCategorySet(), (Integer) instance.getValue());
        } else if (instance.getAttribute().equals(ModGearAttributes.IS_LEGENDARY)) {
            return new CCVaultGearAttribute("Legendary", new AffixCategorySet());
        } else if (instance.getAttribute().equals(ModGearAttributes.UNIQUE_ITEM_KEY)) {
            return new ValueAttribute<String>("Unique Key", new AffixCategorySet(),
                    ((ResourceLocation) instance.getValue()).toString());
        } else if (instance.getAttribute().equals(ModGearAttributes.GEAR_NAME)) {
            return new ValueAttribute<String>("Gear Name", new AffixCategorySet(),
                    (String) instance.getValue().toString());
        } else if (instance.getAttribute().equals(ModGearAttributes.GEAR_UNIQUE_POOL)) {
            return new ValueAttribute<String>("Unique Pool", new AffixCategorySet(),
                    ((ResourceLocation) instance.getValue()).toString());
        }
        try {
            // We just try and cast it to see if this instance was cast down from a modifier
            return parse(stack, (VaultGearModifier<?>) instance);

        } catch (ClassCastException e) {
            return new DebugAttribute(instance.toString());
        }
    }

    private static CCVaultGearAttribute parseDeterministicModifier(VaultGearModifier<?> modifier) {
        var value = modifier.getValue();
        // There are a few modifiers we handle differently, mostly the weird ones like
        // Manabloom
        if (value instanceof ManaPerLootAttribute) {
            // Because this has 2 values
            Pair<Integer, Float> v = new Pair<>(((ManaPerLootAttribute) value).getManaGenerated(),
                    ((ManaPerLootAttribute) value).getManaGenerationChance());
            return new CCManaPerLootAttribute(modifier.getCategories(), v, 0);
        } else if (value instanceof Boolean) {
            // Having a value for boolean modifiers makes no sense, they are either True, or
            // arent there for us to read ever
            // So to avoid obviously duplicated data we just return the fact they exist
            return new CCVaultGearAttribute(modifier.getAttribute().getReader().getModifierName(),
                    modifier.getCategories());
        }
        // Other types we put them in a generic value attribute
        return new TieredValueAttribute(modifier.getAttribute().getReader().getModifierName(), new AffixCategorySet(),
                value, 0);
    }
}
