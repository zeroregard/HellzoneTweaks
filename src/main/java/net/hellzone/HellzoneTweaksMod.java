package net.hellzone;

import net.fabricmc.api.ModInitializer;
import net.hellzone.mixin.BrewingRecipeRegistryMixin;
import net.hellzone.potions.HastePotion;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;

public class HellzoneTweaksMod implements ModInitializer {

	@Override
	public void onInitialize() {
		var hastePotion = new HastePotion();
		Registry.register(Registry.POTION, "hellzone:potion_of_haste", hastePotion);	
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, Items.AMETHYST_SHARD,hastePotion);

	}
}
