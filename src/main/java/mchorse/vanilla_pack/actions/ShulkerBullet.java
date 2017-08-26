package mchorse.vanilla_pack.actions;

import javax.annotation.Nullable;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;

public class ShulkerBullet implements IAction
{
    @Override
    public void execute(EntityLivingBase target, @Nullable AbstractMorph morph)
    {
        World world = target.worldObj;

        if (world.isRemote)
        {
            return;
        }

        if (target instanceof EntityPlayer && ((EntityPlayer) target).getCooledAttackStrength(0.0F) < 1)
        {
            return;
        }

        Entity toShoot = EntityUtils.getTargetEntity(target, 32);

        if (toShoot != null)
        {
            target.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.2F + 1.0F);

            EntityShulkerBullet fireball = new EntityShulkerBullet(world, target, toShoot, Axis.Z);

            fireball.posX = target.posX;
            fireball.posZ = target.posZ;

            world.spawnEntityInWorld(fireball);
        }

        if (target instanceof EntityPlayer)
        {
            ((EntityPlayer) target).resetCooldown();
        }
    }
}