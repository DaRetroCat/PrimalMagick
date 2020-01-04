package com.verdantartifice.primalmagic.common.spells.payloads;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagic.common.spells.SpellPackage;
import com.verdantartifice.primalmagic.common.spells.SpellProperty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractDamageSpellPayload extends AbstractSpellPayload {
    public AbstractDamageSpellPayload() {
        super();
    }
    
    public AbstractDamageSpellPayload(int power) {
        super();
        this.getProperty("power").setValue(power);
    }
    
    @Override
    protected Map<String, SpellProperty> initProperties() {
        Map<String, SpellProperty> propMap = super.initProperties();
        propMap.put("power", new SpellProperty("power", "primalmagic.spell.property.power", 1, 5));
        return propMap;
    }
    
    protected abstract float getTotalDamage(Entity target, SpellPackage spell);
    
    protected DamageSource getDamageSource(Entity target, LivingEntity source) {
        return DamageSource.causeThrownDamage(target, source);
    }

    @Override
    public void execute(RayTraceResult target, Vec3d burstPoint, SpellPackage spell, World world, PlayerEntity caster) {
        if (target != null && target.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityTarget = (EntityRayTraceResult)target;
            if (entityTarget.getEntity() != null) {
                entityTarget.getEntity().attackEntityFrom(this.getDamageSource(entityTarget.getEntity(), caster), this.getTotalDamage(entityTarget.getEntity(), spell));
            }
        }
        this.applySecondaryEffects(target, burstPoint, spell, world, caster);
    }
    
    protected void applySecondaryEffects(@Nullable RayTraceResult target, @Nullable Vec3d burstPoint, @Nonnull SpellPackage spell, @Nonnull World world, @Nonnull LivingEntity caster) {
        // Do nothing by default
    }
}
