package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class CCAbilityCDAttribute extends RangedValueAttribute<Float> {
    private String ability;

    public CCAbilityCDAttribute(AffixCategorySet categories, String ability, int tier, Float value, Float min, Float max) {
        super("Ability Cooldown", categories, value, tier, min, max);
        this.ability = ability;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("ability", this.ability);
        return map;
    }

}
