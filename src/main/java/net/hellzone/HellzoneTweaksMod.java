package net.hellzone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.hellzone.blocks.MagmaSpongeBlock;
import net.hellzone.blocks.HotMagmaSpongeBlock;
import net.hellzone.mixin.BrewingRecipeRegistryMixin;
import net.hellzone.potions.HastePotion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HellzoneTweaksMod implements ModInitializer {
	public static final String MOD_ID = "hellzone";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Block MAGMA_SPONGE_BLOCK = new MagmaSpongeBlock(FabricBlockSettings.copy(Blocks.SPONGE), 6, 64);
	public static final Block HOT_MAGMA_SPONGE_BLOCK = new HotMagmaSpongeBlock(FabricBlockSettings.copy(Blocks.SPONGE), MAGMA_SPONGE_BLOCK.getDefaultState());

	@Override
	public void onInitialize() {
		addHastePotionFeature();
		addMagmaSpongeFeature();
	}

	private void addHastePotionFeature() {
		var hastePotion = new HastePotion();
		Registry.register(Registry.POTION, new Identifier(MOD_ID, "potion_of_haste"), hastePotion);	
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, Items.AMETHYST_SHARD,hastePotion);
	}

	private void addMagmaSpongeFeature() {
		// add the blocks
		((MagmaSpongeBlock)MAGMA_SPONGE_BLOCK).setHotSponge(HOT_MAGMA_SPONGE_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "magma_sponge"), MAGMA_SPONGE_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "hot_magma_sponge"), HOT_MAGMA_SPONGE_BLOCK);
		// add the items
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "magma_sponge"), new BlockItem(MAGMA_SPONGE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "hot_magma_sponge"), new BlockItem(HOT_MAGMA_SPONGE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
	}
}
