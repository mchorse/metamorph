package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Water breath ability
 * 
 * This ability grants its owner ability to stay in water and refill its air. 
 */
public class WaterBreath extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        if (target.isInWater())
        {
            target.setAir(300);
        }
        else
        {
            target.attackEntityFrom(DamageSource.drown, target.worldObj.getDifficulty().equals(EnumDifficulty.PEACEFUL) ? 1.0F : 0.5F);
        }
    }

    /**
     * On morph, hide air bar in HUD
     * 
     * SideOnly annotation needed to remove this method from server (since 
     * it will likely cause {@link NoClassDefFoundError} on dedicated server.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void onMorph(EntityLivingBase target)
    {
        if (target.worldObj.isRemote && target == Minecraft.getMinecraft().thePlayer)
        {
            GuiIngameForge.renderAir = false;
        }
    }

    /**
     * On demorph, show back air bar in HUD
     * 
     * SideOnly annotation needed to remove this method from server (since 
     * it will likely cause {@link NoClassDefFoundError} on dedicated server.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void onDemorph(EntityLivingBase target)
    {
        if (target.worldObj.isRemote && target == Minecraft.getMinecraft().thePlayer)
        {
            GuiIngameForge.renderAir = true;
        }
    }
}