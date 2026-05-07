package com.mrbysco.boombox.datagen.server;

import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class BoomboxRecipeProvider extends RecipeProvider {
	public BoomboxRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		super(provider, recipeOutput);
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

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			return new BoomboxRecipeProvider(provider, recipeOutput);
		}

		@Override
		public String getName() {
			return "Boombox Recipes";
		}
	}
}
