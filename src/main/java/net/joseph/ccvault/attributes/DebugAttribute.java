package net.joseph.ccvault.attributes;

import java.util.HashMap;

public class DebugAttribute extends CCVaultGearAttribute {
    Object data;    

    public DebugAttribute(Object data){
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
