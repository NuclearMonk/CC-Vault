package net.joseph.ccvault.attributes;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class CCAbilityTriggeronDamageAttribute extends RangedValueAttribute<Pair<Integer, Float>> {
    private String ability;
    
    public CCAbilityTriggeronDamageAttribute(AffixCategorySet categories,String ability, Pair<Integer,Float> value, int tier,Pair<Integer,Float> min, Pair<Integer,Float> max ) {
        super("On Hit Ability Cast", categories, value, tier, min, max);
        this.ability = ability;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("ability", ability);
        return map;
    }
}
