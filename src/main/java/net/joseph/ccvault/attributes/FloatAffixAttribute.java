package net.joseph.ccvault.attributes;

import java.util.HashMap;

public class FloatAffixAttribute extends AffixAttribute {
    Float value;
    Float min;
    Float max;

    public FloatAffixAttribute(String name, Float value, Float min, Float max) {
        super(name);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("value", this.value);
        map.put("min", this.min);
        map.put("max", this.max);
        return map;
    }
}
