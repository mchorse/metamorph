package mchorse.metamorph.api.actions;

import mchorse.metamorph.api.IAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Fireball action
 * 
 * This action is responsible for shooting a fireball from player's face. Used 
 * by ghast and blaze morphs.
 */
public class Fireball implements IAction
{
    @Override
    public void execute(EntityPlayer player)
    {
        World world = player.worldObj;

        if (world.isRemote)
        {
            return;
        }

        Vec3d vec3d = player.getLook(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.xCoord * d1;
        double d3 = vec3d.yCoord * d1;
        double d4 = vec3d.zCoord * d1;

        world.playEvent((EntityPlayer) null, 1016, new BlockPos(player), 0);

        EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, player, d2, d3, d4);

        entitylargefireball.explosionPower = 1;
        entitylargefireball.posX = player.posX;
        entitylargefireball.posY = player.posY + player.height * 0.9;
        entitylargefireball.posZ = player.posZ;

        world.spawnEntityInWorld(entitylargefireball);
    }
}