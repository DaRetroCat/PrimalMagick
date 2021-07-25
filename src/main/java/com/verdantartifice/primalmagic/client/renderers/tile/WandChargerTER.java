package com.verdantartifice.primalmagic.client.renderers.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.verdantartifice.primalmagic.common.tiles.mana.WandChargerTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Custom tile entity renderer for wand charger blocks.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagic.common.blocks.mana.WandChargerBlock}
 */
@OnlyIn(Dist.CLIENT)
public class WandChargerTER implements BlockEntityRenderer<WandChargerTileEntity> {
    protected Level level;
    
    public WandChargerTER(BlockEntityRendererProvider.Context context) {
        this.level = context.getBlockEntityRenderDispatcher().level;
    }
    
    @Override
    public void render(WandChargerTileEntity tileEntityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack wandStack = tileEntityIn.getSyncedStackInSlot(1).copy();
        if (!wandStack.isEmpty()) {
            // Render the wand in the center of the charger
            wandStack.setCount(1);
            int rot = (int)(this.level.getLevelData().getGameTime() % 360);
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 0.5D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));   // Spin the wand around its Y-axis
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer().renderStatic(wandStack, ItemTransforms.TransformType.GUI, combinedLight, combinedOverlay, matrixStack, buffer);
            matrixStack.popPose();
        }
    }
}
