package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class TieredValueAttribute<T> extends ValueAttribute<T>{
    protected int tier;

    protected TieredValueAttribute(String name, int tier, T value, AffixCategorySet categories) {
        super(name,value, categories);
        this.tier = tier;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("tier", this.tier);
        return map;
    }
}
