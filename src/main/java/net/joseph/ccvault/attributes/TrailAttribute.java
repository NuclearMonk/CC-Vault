package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class TrailAttribute extends RangedValueAttribute<Integer>{
    String effect_name;
    public TrailAttribute(String effect_name, AffixCategorySet categories, Integer value, int tier, Integer min,
            Integer max) {
        super("Effect", categories, value, tier, min, max);
        this.effect_name = effect_name;
    }
    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("effect", effect_name);
        return map;
    }

    
}
