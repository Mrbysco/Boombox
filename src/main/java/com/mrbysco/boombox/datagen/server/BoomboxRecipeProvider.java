package com.mrbysco.boombox.datagen.server;

import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;

public class BoomboxRecipeProvider extends RecipeProvider {
	public BoomboxRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
		super(recipeOutput, advancementOutput);
	}

	@Override
	protected void buildRecipes() {
		shaped(RecipeCategory.MISC, ModRegistry.BOOMBOX.get())
				.pattern(" I ")
				.pattern("INI")
				.pattern(" I ")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('N', Items.NOTE_BLOCK)
				.unlockedBy("has_note_block", has(Items.NOTE_BLOCK))
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.save(output);
	}
}
