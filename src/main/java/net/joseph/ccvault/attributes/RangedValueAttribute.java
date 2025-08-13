package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class RangedValueAttribute<T> extends TieredValueAttribute<T> {
    T min;
    T max;

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public RangedValueAttribute(String name, AffixCategorySet categories, T value, int tier, T min, T max) {
        super(name, categories, value, tier);
        this.min = min;
        this.max = max;
    }

    

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("min", this.min);
        map.put("max", this.max);
        return map;
    }
}
