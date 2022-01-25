package net.hellzone.potions;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;

public class HastePotion extends Potion {
    public HastePotion() {
        // 1200 = 60 seconds...? what is this unit
        super("haste", new StatusEffectInstance(StatusEffects.HASTE, 1200, 1));
    }

    @Override
    public boolean hasInstantEffect() {
        return false;
    }
}
