package mchorse.metamorph.entity;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.util.ObfuscatedName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Replaces the sounds that players usually make when they are in morphs
 */
public class SoundHandler
{
    public static final ObfuscatedName GET_HURT_SOUND = new ObfuscatedName("func_184601_bQ" /* getHurtSound */);
    public static final ObfuscatedName GET_DEATH_SOUND = new ObfuscatedName("func_184615_bR" /* getDeathSound */);
    public static final ObfuscatedName PLAY_STEP_SOUND = new ObfuscatedName("func_180429_a" /* playStepSound */);

    public static final DamageSource GENERIC_DAMAGE = DamageSource.GENERIC;

    public static final SoundEvent NO_SOUND = new SoundEvent(new ResourceLocation(Metamorph.MODID, "no_sound"));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerHurt(LivingAttackEvent event)
    {
        if (event.isCanceled())
        {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayer))
        {
            return;
        }
        IMorphing morphing = Morphing.get((EntityPlayer) entity);
        if (morphing == null)
        {
            return;
        }

        morphing.setLastDamageSource(event.getSource());
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundAtEntityEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        IMorphing morphing = Morphing.get(player);
        if (morphing == null)
        {
            return;
        }
        AbstractMorph morph = morphing.getCurrentMorph();
        if (morph == null)
        {
            return;
        }

        SoundEvent sound = event.getSound();
        if (sound == null) {
            // Sounds can be null, apparently
            return;
        }
        ResourceLocation soundResource = sound.getRegistryName();
        if (soundResource == null) {
            return;
        }
        String soundType = soundResource.getResourcePath();
        
        if (soundType.endsWith(".hurt"))
        {
            SoundEvent newSound = morph.getHurtSound(player, morphing.getLastDamageSource());
            if (newSound == NO_SOUND)
            {
                event.setCanceled(true);
            }
            else if (newSound != null)
            {
                event.setSound(newSound);
            }
        }
        else if (soundType.endsWith(".death"))
        {
            SoundEvent newSound = morph.getDeathSound(player);
            if (newSound == NO_SOUND)
            {
                event.setCanceled(true);
            }
            else if (newSound != null)
            {
                event.setSound(newSound);
            }
        }
        else if (soundType.endsWith(".step"))
        {
            if (player.width != morph.getWidth(player) || player.height != morph.getHeight(player))
            {
                /*
                 * Check if the player's current hitbox does not
                 * match the morph hitbox.
                 * 
                 * Each tick, the player entity attempts to revert
                 * back to the default player hitbox. If the current
                 * morph hitbox is smaller than the player hitbox,
                 * movement can be triggered, which in turn triggers
                 * a step sound.
                 */
                event.setCanceled(true);
            }
            else if (morph.hasCustomStepSound(player))
            {
                event.setCanceled(true);
                morph.playStepSound(player);
            }
        }
    }
}
