package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class SpecialAbilityAttribute<T> extends RangedAttribute<T>{
    private String abilityKey;
    private String modificationType;

    public SpecialAbilityAttribute(String abilityKey,String modificationKey, int tier, T value, T min, T max,
            AffixCategorySet categories) {
        super("Ability Modification", tier, value, min, max, categories);
        this.abilityKey = abilityKey;
        this.modificationType = modificationKey;
    }
    @Override
    public HashMap<String, Object> toLuaTable() {
        var map =  super.toLuaTable();
        map.put("ability", this.abilityKey);
        map.put("modification", this.modificationType);
        return map;
    }
    
}
