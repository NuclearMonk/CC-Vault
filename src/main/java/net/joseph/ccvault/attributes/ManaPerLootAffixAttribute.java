package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

public class ManaPerLootAffixAttribute extends AffixAttribute{
    
    private ManaPerLootAttribute value;
    private ManaPerLootAttribute min;
    private ManaPerLootAttribute max;

    protected ManaPerLootAffixAttribute(String name, ManaPerLootAttribute value, ManaPerLootAttribute min, ManaPerLootAttribute max) {
        super(name);
        this.value=value;
        this.min=min;
        this.max=max; 
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("value", toLuaTable(this.value));
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
