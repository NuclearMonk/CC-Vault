package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

public class RangedManaPerLootAttribute extends CCManaPerLootAttribute{
    int tier;
    ManaPerLootAttribute min;
    ManaPerLootAttribute max;


    protected RangedManaPerLootAttribute(int tier, ManaPerLootAttribute value, ManaPerLootAttribute min, ManaPerLootAttribute max, AffixCategorySet categories) {
        super(value,categories);
        this.tier = tier;
        this.min = min;
        this.max = max;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("min", toLuaTable(this.min));
        map.put("max", toLuaTable(this.max));
        return map;
    }

}
