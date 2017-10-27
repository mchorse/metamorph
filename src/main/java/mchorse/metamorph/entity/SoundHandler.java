package mchorse.metamorph.entity;

import java.lang.reflect.Method;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.coremod.MetamorphCoremod;
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
    
    /**
     * Ascends up a class chain until it finds the specified method, regardless
     * of access modifier. Assumes finalClazz is the original declarer of the specified method.
     */
    private static Method getPrivateMethod(Class clazz, Class finalClazz, String methodName, Class<?>... paramVarArgs)
            throws NoSuchMethodException, SecurityException
    {
        Method privateMethod = null;
        
        for (Class testClazz = clazz;
                testClazz != finalClazz && privateMethod == null;
                testClazz = testClazz.getSuperclass())
        {
            for (Method method : testClazz.getDeclaredMethods())
            {
                if (!method.getName().equals(methodName))
                {
                    continue;
                }
                
                Class<?>[] parameters = method.getParameterTypes();
                if (!(parameters.length == paramVarArgs.length))
                {
                    continue;
                }
                boolean matchingMethod = true;
                for (int i = 0; i < parameters.length; i++)
                {
                    if (!(parameters[i] == paramVarArgs[i]))
                    {
                        matchingMethod = false;
                        break;
                    }
                }
                
                if (matchingMethod)
                {
                    privateMethod = method;
                    break;
                }
            }
        }
        
        if (privateMethod == null)
        {
            privateMethod = finalClazz.getDeclaredMethod(methodName, paramVarArgs);
        }
        
        privateMethod.setAccessible(true);
        return privateMethod;
    }
    
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
            Method methodHurtSound = getPrivateMethod(soundEntity.getClass(),
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
            Method methodDeathSound = getPrivateMethod(soundEntity.getClass(),
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
            Method methodPlayStep = getPrivateMethod(soundEntity.getClass(),
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
