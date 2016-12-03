package mchorse.vanilla_pack.abilities;

import net.minecraft.entity.player.EntityPlayer;
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
    public void update(EntityPlayer player)
    {
        if (player.isInWater())
        {
            player.setAir(300);
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
    public void onMorph(EntityPlayer player)
    {
        if (player.worldObj.isRemote)
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
    public void onDemorph(EntityPlayer player)
    {
        if (player.worldObj.isRemote)
        {
            GuiIngameForge.renderAir = true;
        }
    }
}