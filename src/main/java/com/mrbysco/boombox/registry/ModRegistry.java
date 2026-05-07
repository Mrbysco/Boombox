package com.mrbysco.boombox.registry;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.block.BoomboxBlock;
import com.mrbysco.boombox.block.blockentity.BoomboxBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BoomboxMod.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoomboxMod.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BoomboxMod.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoomboxMod.MOD_ID);

	public static final DeferredBlock<BoomboxBlock> BOOMBOX = BLOCKS.registerBlock("boombox", BoomboxBlock::new, () -> BlockBehaviour.Properties.ofLegacyCopy(Blocks.NOTE_BLOCK).strength(1));

	public static final DeferredItem<BlockItem> BOOMBOX_ITEM = ITEMS.registerSimpleBlockItem(BOOMBOX);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoomboxBlockEntity>> BOOMBOX_BE = BLOCK_ENTITY_TYPES.register("boombox", () ->
			new BlockEntityType<>(BoomboxBlockEntity::new, ModRegistry.BOOMBOX.get()));

	public static final Supplier<CreativeModeTab> BOOMBOX_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(ModRegistry.BOOMBOX.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.boombox"))
			.displayItems((features, output) -> {
				List<ItemStack> stacks = ModRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());
}
