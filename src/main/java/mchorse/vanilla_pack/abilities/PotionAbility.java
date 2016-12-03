package mchorse.vanilla_pack.abilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Abstract potion ability
 * 
 * This is class is responsible for adding specific given potion effect from 
 * subclasses. You can also give the duration for the potion effect. 
 */
public abstract class PotionAbility extends Ability
{
    protected Potion potion;
    protected int duration = 1200;

    @Override
    public void update(EntityPlayer player)
    {
        if (!player.isPotionActive(this.potion))
        {
            this.onMorph(player);
        }
    }

    @Override
    public void onMorph(EntityPlayer player)
    {
        player.addPotionEffect(new PotionEffect(this.potion, this.duration, 0, false, false));
    }

    @Override
    public void onDemorph(EntityPlayer player)
    {
        player.removePotionEffect(this.potion);
    }
}