package net.joseph.ccvault.attributes;

import java.util.HashMap;

public class ValueAttribute<T> extends CCVaultGearAttribute {
    protected T value;
    public T getValue() {
        return value;
    }
    public ValueAttribute(String name, T value) {
        super(name);
        this.value = value;
    }
    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("value", this.value);
        return map;
    }
    
}
