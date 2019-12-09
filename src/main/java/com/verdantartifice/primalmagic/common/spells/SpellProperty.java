package com.verdantartifice.primalmagic.common.spells;

import javax.annotation.Nonnull;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SpellProperty {
    protected int value;
    protected final String name;
    protected final String translationKey;
    protected final int min;
    protected final int max;
    
    public SpellProperty(@Nonnull String name, @Nonnull String translationKey, int min, int max) {
        this.name = name;
        this.translationKey = translationKey;
        this.min = min;
        this.max = max;
        this.value = min;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ITextComponent getDescription() {
        return new TranslationTextComponent(this.translationKey);
    }
    
    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getValue() {
        return this.value;
    }
    
    public void setValue(int newValue) {
        this.value = MathHelper.clamp(newValue, this.min, this.max);
    }
}
