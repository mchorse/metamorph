package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.api.abilities.Ability;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.GuiIngameForge;

/**
 * Swim ability
 * 
 * This ability makes player a better swimmer. What it does, it basically 
 * increases the swim speed and also gives player more control over vertical 
 * movement. 
 */
public class Swim extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        updateMotion(target);
        updateAir(target);
    }
    
    private void updateMotion(EntityLivingBase target)
    {
        double speed = 0.35;

        if (target.isInWater())
        {
            if (target.moveForward != 0 || target.moveStrafing != 0)
            {
                float a = target.rotationYaw / 180 * (float) Math.PI;

                float f1 = MathHelper.sin(a);
                float f2 = MathHelper.cos(a);

                target.motionX = (double) (target.moveStrafing * f2 - target.moveForward * f1) * speed;
                target.motionZ = (double) (target.moveForward * f2 + target.moveStrafing * f1) * speed;
                target.motionY = -MathHelper.sin((float) (target.rotationPitch / 180 * Math.PI)) * target.moveForward * speed;
            }
            else
            {
                target.motionX *= 0.6;
                target.motionY *= 0.6;

                if (target.motionY < 0)
                {
                    target.motionY = 0;
                }
            }
        }
    }
    
    private void updateAir(EntityLivingBase target)
    {
        if (target instanceof EntityPlayer)
        {
            IMorphing morphing = Morphing.get((EntityPlayer)target);
            if (morphing != null)
            {
                if (target.isInWater())
                {
                    morphing.setSquidAir(300);
                }
                else
                {
                    int air = morphing.getSquidAir() - 1;
                    if (air <= -20)
                    {
                        air = 0;
                        target.attackEntityFrom(DamageSource.drown, 2.0F);
                    }
                    morphing.setSquidAir(air);
                }
                // Update air render state if we are client-side
                // Boolean flags must be set every tick since it is
                // reset during each call to AbstractMorph.update
                if (target.worldObj.isRemote)
                {
                    GuiIngameForge.renderAir = false;
                    ClientProxy.hud.renderSquidAir = true;
                    ClientProxy.hud.squidAir = morphing.getSquidAir();
                }
            }
        }
    }
    
    @Override
    public void onMorph(EntityLivingBase target)
    {
        IMorphing morphing = Morphing.get((EntityPlayer)target);
        if (morphing != null)
        {
            morphing.setSquidAir(target.getAir());
        }
        if (target.worldObj.isRemote)
        {
            GuiIngameForge.renderAir = false;
            ClientProxy.hud.renderSquidAir = true;
        }
    }
    
    @Override
    public void onDemorph(EntityLivingBase target)
    {
        IMorphing morphing = Morphing.get((EntityPlayer)target);
        if (morphing != null)
        {
            target.setAir(morphing.getSquidAir());
        }
        if (target.worldObj.isRemote)
        {
            GuiIngameForge.renderAir = true;
            ClientProxy.hud.renderSquidAir = false;
        }
    }
}