package com.verdantartifice.primalmagick.common.blocks.trees;

import java.util.Random;

import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

/**
 * Base definition for log blocks that phase in and out over time.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractPhasingLogBlock extends StrippableLogBlock {
    public static final EnumProperty<TimePhase> PHASE = EnumProperty.create("phase", TimePhase.class);
    
    public AbstractPhasingLogBlock(Block stripped, Block.Properties properties) {
        super(stripped, properties);
        this.setDefaultState(this.getDefaultState().with(PHASE, TimePhase.FULL));
    }
    
    /**
     * Get the current phase of the block based on the current game time.
     * 
     * @param world the game world
     * @return the block's current phase
     */
    public abstract TimePhase getCurrentPhase(IWorld world);
    
    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PHASE);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // Set the block's phase upon placement
        TimePhase phase = this.getCurrentPhase(context.getWorld());
        return super.getStateForPlacement(context).with(PHASE, phase);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        // Periodically check to see if the block's phase needs to be updated
        super.randomTick(state, worldIn, pos, random);
        TimePhase newPhase = this.getCurrentPhase(worldIn);
        if (newPhase != state.get(PHASE)) {
            worldIn.setBlockState(pos, state.with(PHASE, newPhase), Constants.BlockFlags.DEFAULT);
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        // Immediately check to see if the block's phase needs to be updated when one of its neighbors changes
        BlockState state = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        TimePhase newPhase = this.getCurrentPhase(worldIn);
        if (newPhase != state.get(PHASE)) {
            state = state.with(PHASE, newPhase);
        }
        return state;
    }
    
    @Override
    protected BlockState getDefaultStrippedState(BlockState originalState) {
        return super.getDefaultStrippedState(originalState).with(PHASE, originalState.get(PHASE));
    }
}
