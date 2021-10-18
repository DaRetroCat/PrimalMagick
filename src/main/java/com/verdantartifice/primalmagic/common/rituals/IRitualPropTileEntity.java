package com.verdantartifice.primalmagic.common.rituals;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;

/**
 * Interface indicating whether a tile entity can serve as a prop for magical rituals.
 * 
 * @author Daedalus4096
 */
public interface IRitualPropTileEntity {
    public boolean isPropOpen();
    
    public void setPropOpen(boolean open);
    
    @Nullable
    public BlockPos getAltarPos();

    public void setAltarPos(@Nullable BlockPos pos);

    public void notifyAltarOfPropActivation(float stabilityBonus);
}
