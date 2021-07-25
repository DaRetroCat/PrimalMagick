package com.verdantartifice.primalmagic.common.items.entities;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.entities.misc.FlyingCarpetEntity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

/**
 * Item definition for a flying carpet.  Spawns a flying carpet entity when used for the player to
 * ride around on.
 * 
 * @author Daedalus4096
 */
public class FlyingCarpetItem extends Item {
    public static final ResourceLocation COLOR_PROPERTY = new ResourceLocation(PrimalMagic.MODID, "color");

    public FlyingCarpetItem(Item.Properties properties) {
        super(properties);
    }
    
    @SuppressWarnings("deprecation")
    public static ItemPropertyFunction getColorProperty() {
        return new ItemPropertyFunction() {
            @OnlyIn(Dist.CLIENT)
            @Override
            public float call(ItemStack stack, ClientLevel world, LivingEntity entity, int unknown) {
                DyeColor color = null;
                if (stack != null && stack.getItem() instanceof FlyingCarpetItem) {
                    color = ((FlyingCarpetItem)stack.getItem()).getDyeColor(stack);
                }
                if (color == null) {
                    // Default to white if no dye color is applied
                    color = DyeColor.WHITE;
                }
                return ((float)color.getId() / 16.0F);
            }
        };
    }
    
    public static ItemStack dyeCarpet(ItemStack carpetStack, DyeItem dye) {
        if (carpetStack.getItem() instanceof FlyingCarpetItem) {
            ItemStack retVal = carpetStack.copy();
            ((FlyingCarpetItem)retVal.getItem()).setDyeColor(retVal, dye.getDyeColor());
            return retVal;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (!world.isClientSide && state.getBlock() == Blocks.CAULDRON) {
            int level = state.getValue(CauldronBlock.LEVEL).intValue();
            if (level > 0) {
                this.removeDyeColor(stack);
                ((CauldronBlock)Blocks.CAULDRON).setWaterLevel(world, pos, state, level - 1);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else if (!world.isClientSide) {
            if (context.getClickedFace() != Direction.UP) {
                return InteractionResult.PASS;
            }
            double posX = context.getClickLocation().x;
            double posY = context.getClickLocation().y;
            double posZ = context.getClickLocation().z;
            FlyingCarpetEntity entityCarpet = new FlyingCarpetEntity(world, posX, posY, posZ);
            if (stack.hasTag()) {
                entityCarpet.setDyeColor(this.getDyeColor(stack));
            }
            entityCarpet.setYRot(context.getPlayer().getYRot());
            world.addFreshEntity(entityCarpet);
            world.playSound(null, posX, posY, posZ, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }
    
    public DyeColor getDyeColor(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (nbt != null && nbt.contains("display", Constants.NBT.TAG_COMPOUND)) {
                CompoundTag displayNbt = nbt.getCompound("display");
                if (displayNbt != null && displayNbt.contains("color", Constants.NBT.TAG_INT)) {
                    return DyeColor.byId(displayNbt.getInt("color"));
                }
            }
        }
        return null;
    }
    
    public void setDyeColor(ItemStack stack, DyeColor color) {
        if (color == null) {
            return;
        }
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        CompoundTag nbt = stack.getTag();
        if (!nbt.contains("display", Constants.NBT.TAG_COMPOUND)) {
            nbt.put("display", new CompoundTag());
        }
        nbt.getCompound("display").putInt("color", color.getId());
    }
    
    public void removeDyeColor(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (nbt != null && nbt.contains("display", Constants.NBT.TAG_COMPOUND)) {
                CompoundTag displayNbt = nbt.getCompound("display");
                if (displayNbt != null && displayNbt.contains("color", Constants.NBT.TAG_INT)) {
                    displayNbt.remove("color");
                }
            }
        }
    }
}
