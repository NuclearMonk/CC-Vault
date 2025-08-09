package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class RangedAttribute<T> extends TieredValueAttribute<T> {
    T min;
    T max;

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public RangedAttribute(String name,int tier, T value, T min, T max, AffixCategorySet categories) {
        super(name,tier, value, categories);
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
