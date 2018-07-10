package mchorse.vanilla_pack.actions;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class Spit implements IAction
{
    @SuppressWarnings("unused")
	@Override
    public void execute(EntityLivingBase target, AbstractMorph morph)
    {
        if (target.world.isRemote)
        {
            return;
        }

        EntityLlamaSpit spit = new EntityLlamaSpit(target.world);

        Vec3d vec3d = target.getLook(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.x;
        double d3 = vec3d.y;
        double d4 = vec3d.z;

        spit.setPosition(target.posX + d2 * 1.5, target.posY + target.height * 0.8F, target.posZ + d4 * 1.5);
        spit.shoot(d2, d3, d4, (float) 1.0F, 1.0F);

        if (morph instanceof EntityMorph)
        {
            EntityLivingBase entity = ((EntityMorph) morph).getEntity();

            if (entity instanceof EntityLlama)
            {
                spit.owner = (EntityLlama) entity;
            }
        }

        target.world.playSound((EntityPlayer) null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_LLAMA_SPIT, target.getSoundCategory(), 1.0F, 1.0F + (target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.2F);
        target.world.spawnEntity(spit);
    }
}