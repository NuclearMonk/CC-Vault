package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class CCAbilityLevelAttribute extends RangedValueAttribute<Integer> {
    private String ability;

    public CCAbilityLevelAttribute(String ability, int tier, Integer value, Integer min, Integer max,
            AffixCategorySet categories) {
        super("Ability Level Increase", categories, value, tier, min, max);
        this.ability = ability;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("ability", this.ability);
        return map;
    }

}
