package mchorse.vanilla_pack.actions;

import javax.annotation.Nullable;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Ender dragon's fire breath 
 */
public class FireBreath implements IAction
{
    @Override
    public void execute(EntityLivingBase target, @Nullable AbstractMorph morph)
    {
        if (target.world.isRemote)
        {
            return;
        }

        if (target instanceof EntityPlayer && ((EntityPlayer) target).getCooledAttackStrength(0.0F) < 1)
        {
            return;
        }

        Vec3d vec3d = target.getLook(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.xCoord * d1;
        double d3 = vec3d.yCoord * d1;
        double d4 = vec3d.zCoord * d1;

        target.world.playEvent((EntityPlayer) null, 1017, new BlockPos(target), 0);

        EntityDragonFireball fireball = new EntityDragonFireball(target.world, target, d2, d3, d4);

        fireball.posX = target.posX + d2 / d1;
        fireball.posY = target.posY + target.height * 0.9;
        fireball.posZ = target.posZ + d4 / d1;

        target.world.spawnEntity(fireball);

        if (target instanceof EntityPlayer)
        {
            ((EntityPlayer) target).resetCooldown();
        }
    }
}