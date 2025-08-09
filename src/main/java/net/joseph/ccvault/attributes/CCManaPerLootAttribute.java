package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

public class CCManaPerLootAttribute extends CCVaultGearAttribute{
    
    ManaPerLootAttribute value;

    public CCManaPerLootAttribute(ManaPerLootAttribute value, AffixCategorySet categories) {
        super("Manabloom", categories);
        this.value = value;
    }

    
    
    public HashMap<String, Object> toLuaTable(ManaPerLootAttribute attr) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Mana", attr.getManaGenerated());
        map.put("Chance", attr.getManaGenerationChance());
        return map;
    }



    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map =  super.toLuaTable();
        map.put("value", this.toLuaTable(value));
        return map;
    }
}
