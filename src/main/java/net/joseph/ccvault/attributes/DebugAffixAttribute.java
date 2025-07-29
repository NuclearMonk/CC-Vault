package net.joseph.ccvault.attributes;

import java.util.HashMap;

public class DebugAffixAttribute extends AffixAttribute {
    Object data;    

    public DebugAffixAttribute(Object data){
        super("debug");
        this.data = data;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("data", this.data);
        return map;

    }
}
