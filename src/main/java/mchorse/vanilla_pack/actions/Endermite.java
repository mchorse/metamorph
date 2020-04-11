package mchorse.vanilla_pack.actions;

import java.util.Random;

import javax.annotation.Nullable;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

/**
 * Endermite action
 * 
 * Teleports player to a random location nearby.
 */
public class Endermite implements IAction
{
    @Override
    public void execute(EntityLivingBase target, @Nullable AbstractMorph morph)
    {
        Random rand = target.getRNG();

        /* Teleports within 32 block radius */
        double x = target.posX + (rand.nextDouble() - 0.5D) * 64.0D;
        double y = target.posY + (double) (rand.nextInt(64) - 32);
        double z = target.posZ + (rand.nextDouble() - 0.5D) * 64.0D;

        if (target.attemptTeleport(x, y, z))
        {
            target.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            target.world.playSound((EntityPlayer) null, target.prevPosX, target.prevPosY, target.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, target.getSoundCategory(), 1.0F, 1.0F);
        }
    }
}