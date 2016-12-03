package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

/**
 * Hungerless ability
 * 
 * This ability is responsible for removing hunger potion effect from given 
 * morph. Really good for zombies, since they're the only one who're going 
 * to use them.
 */
public class Hungerless extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        if (player.isPotionActive(MobEffects.HUNGER))
        {
            this.onMorph(player);
        }
    }

    @Override
    public void onMorph(EntityPlayer player)
    {
        player.removePotionEffect(MobEffects.HUNGER);
    }
}