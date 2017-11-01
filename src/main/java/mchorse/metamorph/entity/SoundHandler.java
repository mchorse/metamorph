package mchorse.metamorph.entity;

import java.lang.reflect.Method;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.coremod.MetamorphCoremod;
import mchorse.metamorph.util.InvokeUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Replaces the sounds that players usually make when they are in morphs
 */
public class SoundHandler
{
    private static final String[] GET_HURT_SOUND = new String[]{"getHurtSound", "func_184601_bQ"};
    private static final String[] GET_DEATH_SOUND = new String[]{"getDeathSound", "func_184615_bR"};
    private static final String[] PLAY_STEP_SOUND = new String[]{"playStepSound", "func_180429_a"};
    
    @SubscribeEvent
    public void onPlaySound(PlaySoundAtEntityEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)entity;
        IMorphing morphing = Morphing.get(player);
        if (morphing == null)
        {
            return;
        }
        AbstractMorph morph = morphing.getCurrentMorph();
        if (morph == null || !(morph instanceof EntityMorph))
        {
            return;
        }
        EntityLivingBase soundEntity = ((EntityMorph)morph).getEntity();
        
        String soundType = event.getSound().getRegistryName().getResourcePath();
        if (soundType.endsWith(".hurt"))
        {
            SoundEvent newSound = getHurtSound(soundEntity);
            if (newSound != null)
            {
                event.setSound(newSound);
            }
        }
        else if (soundType.endsWith(".death"))
        {
            SoundEvent newSound = getDeathSound(soundEntity);
            if (newSound != null)
            {
                event.setSound(newSound);
            }
        }
        else if (soundType.endsWith(".step"))
        {
            event.setCanceled(true);
            playStepSound(soundEntity);
        }
    }
    
    private static SoundEvent getHurtSound(EntityLivingBase soundEntity)
    {
        try
        {
            Method methodHurtSound = InvokeUtil.getPrivateMethod(soundEntity.getClass(),
                    EntityLivingBase.class,
                    GET_HURT_SOUND[MetamorphCoremod.obfuscated ? 1 : 0]);
            SoundEvent newSound = (SoundEvent)methodHurtSound.invoke(soundEntity);
            if (newSound != null)
            {
                return newSound;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static SoundEvent getDeathSound(EntityLivingBase soundEntity)
    {
        try
        {
            Method methodDeathSound = InvokeUtil.getPrivateMethod(soundEntity.getClass(),
                    EntityLivingBase.class,
                    GET_DEATH_SOUND[MetamorphCoremod.obfuscated ? 1 : 0]);
            SoundEvent newSound = (SoundEvent)methodDeathSound.invoke(soundEntity);
            if (newSound != null)
            {
                return newSound;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static void playStepSound(EntityLivingBase soundEntity)
    {
        try
        {
            Method methodPlayStep = InvokeUtil.getPrivateMethod(soundEntity.getClass(),
                    Entity.class,
                    PLAY_STEP_SOUND[MetamorphCoremod.obfuscated ? 1 : 0],
                    BlockPos.class, Block.class);
            
            int x = MathHelper.floor_double(soundEntity.posX);
            int y = MathHelper.floor_double(soundEntity.posY - 0.20000000298023224D);
            int z = MathHelper.floor_double(soundEntity.posZ);
            BlockPos pos = new BlockPos(x, y, z);
            Block block = soundEntity.worldObj.getBlockState(pos).getBlock();
            
            methodPlayStep.invoke(soundEntity, pos, block);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
