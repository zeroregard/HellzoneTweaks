package net.hellzone;

import net.fabricmc.api.ModInitializer;
import net.hellzone.potions.HastePotion;
import net.minecraft.util.registry.Registry;

public class HellzoneTweaksMod implements ModInitializer {

	@Override
	public void onInitialize() {
		Registry.register(Registry.POTION, "hellzone:potion_of_haste", new HastePotion());	
	}
}
