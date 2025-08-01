package net.joseph.ccvault.attributes;

import java.util.HashMap;

public class CCAbilityLevelAttribute  extends RangedAttribute<Integer>{
    private String ability;
    public CCAbilityLevelAttribute(String ability , Integer value, Integer min, Integer max) {
        super("Ability", value, min, max);
        this.ability= ability;
    }
    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put(ability, this.ability);
        return map;
    }
    
}
