package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a basic moon pixie.  Weakest of the moon pixies.
 * 
 * @author Daedalus4096
 */
public class BasicMoonPixieEntity extends AbstractMoonPixieEntity implements IBasicPixie {
    public BasicMoonPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.BASIC_MOON_PIXIE.get();
    }
}
