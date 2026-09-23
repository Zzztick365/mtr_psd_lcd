package com.mtrpsdlcd.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class MyPSDPillar extends Block {
	public static final Property<Direction> FACING = Properties.HORIZONTAL_FACING;

	public MyPSDPillar() {

		super(((org.mtr.mapping.holder.BlockSettings) org.mtr.mod.Blocks.createDefaultBlockSettings(false)).data);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {

		final Direction horizontal = context.getPlayer() != null ? context.getPlayer().getHorizontalFacing() : context.getPlayerLookDirection().getOpposite();
		final Direction safe = horizontal == Direction.UP || horizontal == Direction.DOWN ? Direction.NORTH : horizontal;
		return getDefaultState().with(FACING, safe);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return shapeFor(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return shapeFor(state);
	}

	private static VoxelShape shapeFor(BlockState state) {
		switch (state.get(FACING)) {
			case SOUTH:
				return Block.createCuboidShape(5, 0, 10, 11, 16, 16);
			case WEST:
				return Block.createCuboidShape(0, 0, 5, 6, 16, 11);
			case EAST:
				return Block.createCuboidShape(10, 0, 5, 16, 16, 11);
			default:
				return Block.createCuboidShape(5, 0, 0, 11, 16, 6);
		}
	}
}
