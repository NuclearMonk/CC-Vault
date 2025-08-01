package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

public class CCManaPerLootAttribute extends RangedAttribute<ManaPerLootAttribute>{
    


    protected CCManaPerLootAttribute(String name, ManaPerLootAttribute value, ManaPerLootAttribute min, ManaPerLootAttribute max) {
        super(name, value, min, max);
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("min", toLuaTable(this.min));
        map.put("max", toLuaTable(this.max));
        return map;
    }
    
    public HashMap<String, Object> toLuaTable(ManaPerLootAttribute attr) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Mana", attr.getManaGenerated());
        map.put("Chance", attr.getManaGenerationChance());
        return map;
    }
}
