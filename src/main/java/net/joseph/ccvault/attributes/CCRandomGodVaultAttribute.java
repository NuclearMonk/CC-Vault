package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import net.minecraft.resources.ResourceLocation;

public class CCRandomGodVaultAttribute extends ValueAttribute<CCTemporalAttribute> {
    public  CCRandomGodVaultAttribute(AffixCategorySet categories,ResourceLocation modifier, int count, int time) {
        super("Temporal Modifier", categories, new CCTemporalAttribute(modifier.toString(), count, time));
   }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        HashMap<String, Object> v = new HashMap<>();
        v.put("Effect", this.value.modifier);
        v.put("Count", this.value.count);
        v.put("Time", this.value.time);
        map.put("value", v);
        return map;
    }
}
