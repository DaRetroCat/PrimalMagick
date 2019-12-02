package com.verdantartifice.primalmagic.common.spells.payloads;

import com.verdantartifice.primalmagic.common.sources.SourceList;
import com.verdantartifice.primalmagic.common.spells.ISpellPackage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class AbstractDamageSpellPayload extends AbstractSpellPayload {
    protected int power;
    
    public AbstractDamageSpellPayload() {}
    
    public AbstractDamageSpellPayload(int power) {
        this.power = power;
    }
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("Power", this.power);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.power = nbt.getInt("Power");
    }
    
    protected abstract float getTotalDamage();

    @Override
    public void execute(RayTraceResult target, ISpellPackage spell, World world, PlayerEntity caster) {
        if (target.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityTarget = (EntityRayTraceResult)target;
            if (entityTarget.getEntity() != null) {
                entityTarget.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(entityTarget.getEntity(), caster), this.getTotalDamage());
            }
        }
    }

    @Override
    public SourceList getManaCost() {
        return new SourceList().add(this.getSource(), this.power);
    }
}
