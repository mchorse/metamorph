package mchorse.vanilla_pack.actions;

import mchorse.metamorph.api.abilities.IAction;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Fireball action
 * 
 * This action is responsible for shooting a fireball from player's face. Used 
 * by ghast morph.
 */
public class Fireball implements IAction
{
    @Override
    public void execute(EntityLivingBase target)
    {
        World world = target.world;

        if (world.isRemote)
        {
            return;
        }

        if (target instanceof EntityPlayer && ((EntityPlayer) target).getCooledAttackStrength(0.0F) < 1)
        {
            return;
        }

        Vec3d vec3d = target.getLook(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.x * d1;
        double d3 = vec3d.y * d1;
        double d4 = vec3d.z * d1;

        world.playEvent((EntityPlayer) null, 1016, new BlockPos(target), 0);

        EntityLargeFireball fireball = new EntityLargeFireball(world, target, d2, d3, d4);

        fireball.explosionPower = 1;
        fireball.posX = target.posX;
        fireball.posY = target.posY + target.height * 0.9;
        fireball.posZ = target.posZ;

        world.spawnEntity(fireball);

        if (target instanceof EntityPlayer)
        {
            ((EntityPlayer) target).resetCooldown();
        }
    }
}