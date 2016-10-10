package mchorse.metamorph.api.abilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * Fire proof ability
 * 
 * This abilitiy grants you fire immunity. So basically you're fire proof.
 */
public class FireProof extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        if (!player.isPotionActive(MobEffects.FIRE_RESISTANCE))
        {
            this.onMorph(player);
        }
    }

    @Override
    public void onMorph(EntityPlayer player)
    {
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 1200, 0, false, false));
    }

    @Override
    public void onDemorph(EntityPlayer player)
    {
        player.removePotionEffect(MobEffects.FIRE_RESISTANCE);
    }
}