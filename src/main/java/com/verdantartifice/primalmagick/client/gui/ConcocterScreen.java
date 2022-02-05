package com.verdantartifice.primalmagick.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.gui.widgets.ManaGaugeWidget;
import com.verdantartifice.primalmagick.common.containers.ConcocterContainer;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI screen for concocter block.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ConcocterScreen extends ContainerScreen<ConcocterContainer> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/concocter.png");
    
    protected ManaGaugeWidget manaGauge;

    public ConcocterScreen(ConcocterContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.titleX = 44;
        this.playerInventoryTitleX = 27;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.manaGauge.setCurrentMana(this.container.getCurrentMana());
        this.manaGauge.setMaxMana(this.container.getMaxMana());
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
    
    @Override
    protected void init() {
        super.init();
        this.manaGauge = this.addButton(new ManaGaugeWidget(this.guiLeft + 10, this.guiTop + 6, Source.INFERNAL, this.container.getCurrentMana(), this.container.getMaxMana()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        // Render background texture
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        // Animate spin progress indicator
        int cook = this.container.getCookProgressionScaled();
        this.blit(matrixStack, this.guiLeft + 103, this.guiTop + 34, 176, 0, cook + 1, 16);
    }
}
