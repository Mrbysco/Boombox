package com.mrbysco.boombox.block;

import com.mojang.serialization.MapCodec;
import com.mrbysco.boombox.block.blockentity.BoomboxBlockEntity;
import com.mrbysco.boombox.client.audio.RadioHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BoomboxBlock extends BaseEntityBlock {

	public static final VoxelShape SHAPE = Block.box(0, 0, 7, 16, 12, 9);
	public static final VoxelShape SHAPE2 = Block.box(7, 0, 0, 9, 12, 16);

	public static final MapCodec<BoomboxBlock> CODEC = simpleCodec(BoomboxBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	public BoomboxBlock(Properties properties) {
		super(properties);

		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			RadioHandler.open(pos);
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new BoomboxBlockEntity(blockPos, blockState);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);
		if (direction.getAxis() == Direction.Axis.Z) {
			return SHAPE;
		} else {
			return SHAPE2;
		}
	}
}
