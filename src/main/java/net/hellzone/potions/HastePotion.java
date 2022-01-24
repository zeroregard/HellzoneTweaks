package net.hellzone.potions;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;

public class HastePotion extends Potion {
    public HastePotion() {
        super("haste", new StatusEffectInstance(StatusEffects.HASTE, 60, 1));
    }

    @Override
    public boolean hasInstantEffect() {
        return false;
    }
}
