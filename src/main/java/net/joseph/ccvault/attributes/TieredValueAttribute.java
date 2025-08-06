package net.joseph.ccvault.attributes;

import java.util.HashMap;

public class TieredValueAttribute<T> extends ValueAttribute<T>{
    protected int tier;

    protected TieredValueAttribute(String name, int tier, T value) {
        super(name,value);
        this.tier = tier;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("tier", this.tier);
        return map;
    }
}
