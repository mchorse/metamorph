package mchorse.metamorph.client;

import java.lang.reflect.Method;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;

public class SoundHooks {
    /**
     * May return a different hurt sound if the player is in a
     * morph, otherwise the given sound.
     */
    public static SoundEvent getHurtSound(SoundEvent defaultSound, EntityPlayer player)
    {
        IMorphing morphing = Morphing.get(player);
        if (morphing != null)
        {
            AbstractMorph morph = morphing.getCurrentMorph();
            if (morph != null && morph instanceof EntityMorph)
            {
                EntityLivingBase entity = ((EntityMorph)morph).getEntity();
                try
                {
                    Method methodHurtSound = entity.getClass().getDeclaredMethod("getHurtSound");
                    methodHurtSound.setAccessible(true);
                    SoundEvent newSound = (SoundEvent)methodHurtSound.invoke(entity);
                    if (newSound != null)
                    {
                        return newSound;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return defaultSound;
    }
    
    /**
     * May return a different death sound if the player is in a
     * morph, otherwise the given sound.
     */
    public static SoundEvent getDeathSound(SoundEvent defaultSound, EntityPlayer player)
    {
        IMorphing morphing = Morphing.get(player);
        if (morphing != null)
        {
            AbstractMorph morph = morphing.getCurrentMorph();
            if (morph != null && morph instanceof EntityMorph)
            {
                EntityLivingBase entity = ((EntityMorph)morph).getEntity();
                try
                {
                    Method methodDeathSound = entity.getClass().getDeclaredMethod("getDeathSound");
                    methodDeathSound.setAccessible(true);
                    SoundEvent newSound = (SoundEvent)methodDeathSound.invoke(entity);
                    if (newSound != null)
                    {
                        return newSound;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return defaultSound;
    }
}
