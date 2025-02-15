package com.verdantartifice.primalmagick.common.runes;

import javax.annotation.Nonnull;

import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

/**
 * Definition of a noun rune data structure.  One of three rune types needed to imbue an enchantment.
 * 
 * @author Daedalus4096
 */
public class NounRune extends Rune {
    public NounRune(@Nonnull String tag) {
        super(tag, Rarity.COMMON, false);
    }
    
    public NounRune(@Nonnull ResourceLocation id) {
        super(id, Rarity.COMMON, false);
    }
    
    @Override
    public RuneType getType() {
        return RuneType.NOUN;
    }
}
