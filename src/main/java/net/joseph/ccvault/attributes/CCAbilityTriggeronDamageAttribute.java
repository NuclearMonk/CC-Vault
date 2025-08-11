package net.joseph.ccvault.attributes;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.ability.AbilityTriggerOnDamageAttribute;

public class CCAbilityTriggeronDamageAttribute extends ValueAttribute<Integer> {
    private String ability;
    private Integer minLevel;
    private Integer maxLevel;
    private Integer minChance;
    private Integer maxChance;
    
    public CCAbilityTriggeronDamageAttribute(String name, Integer value, AffixCategorySet categories) {
        super("On Hit Ability Cast", value, categories);
        //TODO Auto-generated constructor stub
    }
}
