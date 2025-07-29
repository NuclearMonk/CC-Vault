package net.joseph.ccvault.attributes;

import java.util.HashMap;

import net.joseph.ccvault.interfaces.ILuaTable;

public class AffixAttribute implements ILuaTable{
    private String name;

    protected AffixAttribute(String name){
        this.name = name;
    }

    @Override
    public HashMap<String, Object> toLuaTable(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        return map;
    }
}