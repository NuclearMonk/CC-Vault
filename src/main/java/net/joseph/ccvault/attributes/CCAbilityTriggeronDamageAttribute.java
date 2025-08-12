package net.joseph.ccvault.attributes;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class CCAbilityTriggerOnDamageAttribute extends RangedValueAttribute<Pair<Integer, Float>> {
    private String ability;
    
    public CCAbilityTriggerOnDamageAttribute(AffixCategorySet categories,String ability, Pair<Integer,Float> value, int tier,Pair<Integer,Float> min, Pair<Integer,Float> max ) {
        super("On Hit Ability Cast", categories, value, tier, min, max);
        this.ability = ability;
    }

    private static HashMap<String, Object> toLuaTable(Pair<Integer, Float> pair){
        var map = new HashMap<String, Object>();
        map.put("level", pair.getFirst());
        map.put("chance", pair.getSecond());
        return map;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ability", ability);
        map.put("value", toLuaTable(value));
        map.put("min", toLuaTable(value));
        map.put("max", toLuaTable(value));
        return map;
    }
}
