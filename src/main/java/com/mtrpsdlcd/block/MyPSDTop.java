package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.holder.WorldAccess;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.block.BlockPSDDoor;
import org.mtr.mod.block.BlockPSDGlass;
import org.mtr.mod.block.BlockPSDGlassEnd;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MyPSDTop extends BlockPSDTop {

	@Override
	@Nonnull
	public Item asItem2() {
		return com.mtrpsdlcd.registry.Items.PSD_GLASS.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopBE(blockPos, blockState);
	}

	@Override
	@Nonnull
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingItem(world, player, item -> {
			if (item.data instanceof org.mtr.mod.item.ItemBrush) {

				if (!alignDirectionIfNeeded(world, pos, state)) {
					return;
				}

				final BlockState cycled = state.cycle(new Property<>(BlockPSDTop.ARROW_DIRECTION.data));
				world.setBlockState(pos, cycled);
				final int doorArrow = IBlock.getStatePropertySafe(cycled, BlockPSDTop.ARROW_DIRECTION);
				final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
				propagateTop(world, pos, facing.rotateYClockwise(), offsetPos -> {
					final BlockState neighbor = world.getBlockState(offsetPos);
					world.setBlockState(offsetPos, applyDirectionToNeighbor(world, neighbor, offsetPos, doorArrow));
				});
				propagateTop(world, pos, facing.rotateYCounterclockwise(), offsetPos -> {
					final BlockState neighbor = world.getBlockState(offsetPos);
					world.setBlockState(offsetPos, applyDirectionToNeighbor(world, neighbor, offsetPos, doorArrow));
				});
			} else {

				final boolean setPersistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.NONE;
				setState(world, pos, setPersistent);
				propagateTop(world, pos, IBlock.getStatePropertySafe(state, BlockPSDTop.FACING).rotateYClockwise(), offsetPos -> setState(world, offsetPos, setPersistent));
				propagateTop(world, pos, IBlock.getStatePropertySafe(state, BlockPSDTop.FACING).rotateYCounterclockwise(), offsetPos -> setState(world, offsetPos, setPersistent));
			}
		}, null, org.mtr.mod.Items.BRUSH.get(), org.mtr.mapping.holder.Items.getShearsMapped());
	}

	private void setState(World world, BlockPos pos, boolean persistent) {
		final Block blockBelow = world.getBlockState(pos.down()).getBlock();
		final BlockState current = world.getBlockState(pos);
		final BlockState toggled;
		if (persistent) {
			final BlockPSDTop.EnumPersistent type;
			if (blockBelow.data instanceof BlockPSDDoor) {
				type = BlockPSDTop.EnumPersistent.ARROW;
			} else if (blockBelow.data instanceof BlockPSDGlass) {
				type = BlockPSDTop.EnumPersistent.ROUTE;
			} else {
				type = BlockPSDTop.EnumPersistent.BLANK;
			}
			toggled = current.with(new Property<>(BlockPSDTop.PERSISTENT.data), type);
		} else {
			toggled = current.with(new Property<>(BlockPSDTop.PERSISTENT.data), BlockPSDTop.EnumPersistent.NONE);
		}
		world.setBlockState(pos, toggled);
	}

	private void propagateTop(World world, BlockPos pos, Direction direction, Consumer<BlockPos> consumer) {
		for (int i = 1; i <= 1; ++i) {
			final BlockPos offsetPos = pos.offset(direction, i);
			if (isTop(world.getBlockState(offsetPos).getBlock())) {
				consumer.accept(offsetPos);
				propagateTop(world, offsetPos, direction, consumer);
				return;
			}
		}
	}

	private boolean isTop(Block block) {
		return block.data instanceof BlockPSDTop;
	}

	private BlockState applyDirectionToNeighbor(World world, BlockState neighbor, BlockPos neighborPos, int doorArrow) {
		final Block below = world.getBlockState(neighborPos.down()).getBlock();
		if (below.data instanceof BlockPSDGlass) {
			final int glassVal = (doorArrow == 2) ? 2 : 0;
			return neighbor.with(new Property<>(BlockPSDTop.ARROW_DIRECTION.data), glassVal);
		}
		return neighbor.with(new Property<>(BlockPSDTop.ARROW_DIRECTION.data), doorArrow);
	}

	private boolean alignDirectionIfNeeded(World world, BlockPos pos, BlockState state) {
		final int myDir = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
		if (myDir <= 0) {
			return true;
		}
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		final Map<Integer, Integer> counts = new HashMap<>();
		counts.put(myDir, 0);
		collectDirectionSide(world, pos, facing.rotateYClockwise(), counts);
		collectDirectionSide(world, pos, facing.rotateYCounterclockwise(), counts);
		final int majority = pickMajority(counts);
		if (majority > 0 && majority != myDir) {
			world.setBlockState(pos, state.with(new Property<>(BlockPSDTop.ARROW_DIRECTION.data), majority));
			return false;
		}
		return true;
	}

	private void collectDirectionSide(World world, BlockPos pos, Direction dir, Map<Integer, Integer> counts) {
		BlockPos next = pos.offset(dir);
		while (true) {
			final BlockState s = world.getBlockState(next);
			if (!(s.getBlock().data instanceof BlockPSDTop)) {
				return;
			}
			final Block below = world.getBlockState(next.down()).getBlock();
			if (below.data instanceof BlockPSDGlass) {
				return;
			}
			final int d = IBlock.getStatePropertySafe(s, BlockPSDTop.ARROW_DIRECTION);
			if (d > 0) {
				counts.merge(d, 1, Integer::sum);
			}
			next = next.offset(dir);
		}
	}

	private int pickMajority(Map<Integer, Integer> counts) {
		int bestDir = 0, bestCount = 0;
		for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
			final int c = e.getValue();
			if (c > bestCount) {
				bestCount = c;
				bestDir = e.getKey();
			}
		}
		return bestDir;
	}

	private boolean isStandalone() {
		return this instanceof IStandaloneTopModule;
	}

	@Override
	@Nonnull
	public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (isStandalone()) {

			return BlockPSDTop.getActualState(world, pos);
		}
		return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	@Nonnull
	public BlockState getPlacementState2(ItemPlacementContext ctx) {
		if (isStandalone()) {

			return getDefaultState2().with(new Property<>(DirectionHelper.FACING.data), ctx.getPlayerFacing().data);
		}
		return super.getPlacementState2(ctx);
	}

	private boolean isDoubleModule() {
		return this instanceof MyPSDTopLcd8 || this instanceof MyPSDTopLcd9 || this instanceof MyPSDTopLcd10 || this instanceof MyPSDTopLcd11;
	}

	private static boolean isDoubleModule(Block block) {
		return block.data instanceof MyPSDTopLcd8 || block.data instanceof MyPSDTopLcd9 || block.data instanceof MyPSDTopLcd10 || block.data instanceof MyPSDTopLcd11;
	}

	@Override
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (isDoubleModule()) {
			final Direction facing = IBlock.getStatePropertySafe(state, DirectionHelper.FACING);
			final IBlock.EnumSide side = IBlock.getStatePropertySafe(state, IBlock.SIDE_EXTENDED);
			if (side == IBlock.EnumSide.LEFT || side == IBlock.EnumSide.RIGHT) {
				final BlockPos partnerPos = side == IBlock.EnumSide.LEFT ? pos.offset(facing.rotateYClockwise()) : pos.offset(facing.rotateYCounterclockwise());
				if (isDoubleModule(world.getBlockState(partnerPos).getBlock())) {
					world.setBlockState(partnerPos, org.mtr.mapping.holder.Blocks.getAirMapped().getDefaultState(), 35);
				}
			}
		}
		super.onBreak2(world, pos, state, player);
	}
}
