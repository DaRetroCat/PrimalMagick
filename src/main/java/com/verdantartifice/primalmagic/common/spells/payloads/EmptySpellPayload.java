package com.verdantartifice.primalmagic.common.spells.payloads;

import com.verdantartifice.primalmagic.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagic.common.sources.Source;
import com.verdantartifice.primalmagic.common.spells.SpellPackage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EmptySpellPayload extends AbstractSpellPayload {
    public static final String TYPE = "none";

    @Override
    public void execute(RayTraceResult target, Vec3d burstPoint, SpellPackage spell, World world, LivingEntity caster) {
        // Do nothing
    }

    @Override
    public boolean isActive() {
        return false;
    }
    
    @Override
    public Source getSource() {
        return Source.EARTH;
    }

    @Override
    public int getBaseManaCost() {
        return 0;
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        // Do nothing
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }
    
    public static CompoundResearchKey getResearch() {
        return null;
    }
}
