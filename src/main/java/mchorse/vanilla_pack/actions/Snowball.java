package mchorse.vanilla_pack.actions;

import javax.annotation.Nullable;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * Snowball action
 * 
 * Throws a snowball in the look direction of the player. The code is taken 
 * from {@link Fireball#execute(EntityPlayer)} and 
 * {@link EntitySnowman#attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase, float)}
 */
public class Snowball implements IAction
{
    @Override
    public void execute(EntityLivingBase target, @Nullable AbstractMorph morph)
    {
        if (!target.world.isRemote)
        {
            EntitySnowball snowball = new EntitySnowball(target.world, target);
            Vec3d vec3d = target.getLook(1.0F);

            double d1 = 4.0D;
            double d2 = vec3d.xCoord * d1;
            double d3 = vec3d.yCoord * d1;
            double d4 = vec3d.zCoord * d1;

            snowball.setPosition(target.posX, target.posY + target.height * 0.9F, target.posZ);
            snowball.motionX = d2;
            snowball.motionY = d3;
            snowball.motionZ = d4;

            target.playSound(SoundEvents.ENTITY_SNOWMAN_SHOOT, 1.0F, 1.0F / (target.getRNG().nextFloat() * 0.4F + 0.8F));
            target.world.spawnEntity(snowball);
        }
    }
}