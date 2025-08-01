package net.joseph.ccvault.attributes;

import java.util.HashMap;

import net.joseph.ccvault.interfaces.ILuaTable;

public class CCVaultGearAttribute implements ILuaTable{
    private String name;

    public String getName() {
        return name;
    }

    protected CCVaultGearAttribute(String name){
        this.name = name;
    }

    @Override
    public HashMap<String, Object> toLuaTable(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        return map;
    }
}