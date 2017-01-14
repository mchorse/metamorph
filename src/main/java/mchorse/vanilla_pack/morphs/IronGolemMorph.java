package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Iron golem morph
 * 
 * This morph is responsible for making IronGolem morph great again! This morph 
 * is very powerful. I should make him much slower.
 * 
 * Well, I found out that without restricting his motion 
 */
public class IronGolemMorph extends EntityMorph
{
    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        if (target.motionY > 0)
        {
            if (Math.abs(target.motionX) > 0.3) target.motionX *= target.isSprinting() ? 0.3 : 0.7;
            if (Math.abs(target.motionZ) > 0.3) target.motionZ *= target.isSprinting() ? 0.3 : 0.7;

            target.motionY *= 0.9;
        }
        else
        {
            target.motionY *= 1.3;
        }

        super.update(target, cap);
    }

    @Override
    public void attack(Entity target, EntityLivingBase source)
    {
        if (this.entity != null)
        {
            this.entity.attackEntityAsMob(target);
        }

        super.attack(target, source);
    }

    @Override
    public AbstractMorph clone()
    {
        IronGolemMorph morph = new IronGolemMorph();

        morph.name = this.name;

        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.entityData = this.entityData.copy();

        return morph;
    }
}