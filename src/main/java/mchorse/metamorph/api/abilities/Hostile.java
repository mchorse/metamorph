package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.morph.MorphHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Hostile ability
 * 
 * Does literally nothing. Used as a trait for 
 * {@link MorphHandler#onLivingSetAttackTarget(net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent)}. 
 */
public class Hostile extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {}
}