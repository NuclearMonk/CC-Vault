package net.joseph.ccvault.attributes;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

public class RangedManaPerLootAttribute extends RangedValueAttribute<Pair<Integer, Float>>{


    protected RangedManaPerLootAttribute(AffixCategorySet categories,Pair<Integer, Float> value, int tier, Pair<Integer, Float> min, Pair<Integer, Float> max) {
        super("Manabloom",categories,value,tier, min, max);
    }

    private HashMap<String, Object> toLuaTable(Pair<Integer, Float> value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Mana", value.getFirst());
        map.put("Chance", value.getSecond());
        return map;
    }
    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("value", toLuaTable(this.min));
        map.put("min", toLuaTable(this.min));
        map.put("max", toLuaTable(this.max));
        return map;
    }

}
