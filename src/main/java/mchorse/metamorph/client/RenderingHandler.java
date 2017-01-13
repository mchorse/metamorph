package mchorse.metamorph.client;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiOverlay;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Rendering handler
 *
 * This handler is rendering handler which is responsible for few things:
 * 
 * - Overlays (survival morph menu, morph acquiring)
 * - Player model
 */
@SideOnly(Side.CLIENT)
public class RenderingHandler
{
    private GuiSurvivalMorphs overlay;
    private GuiOverlay morphOverlay;

    public RenderingHandler(GuiSurvivalMorphs overlay, GuiOverlay morphOverlay)
    {
        this.overlay = overlay;
        this.morphOverlay = morphOverlay;
    }

    /**
     * Draw HUD additions 
     */
    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Post event)
    {
        ScaledResolution resolution = event.getResolution();

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            if (this.overlay.inGUI == false)
            {
                this.overlay.render(resolution.getScaledWidth(), resolution.getScaledHeight());
            }

            this.morphOverlay.render(resolution.getScaledWidth(), resolution.getScaledHeight());
        }
    }

    /**
     * Render player hook
     * 
     * This method is responsible for rendering player, in case if he's morphed, 
     * into morphed entity. This method is also responsible for down scaling
     * oversized entities in inventory GUIs.
     * 
     * I wish devs at Mojang scissored the inventory area where those the 
     * player model is rendered. 
     */
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing capability = Morphing.get(player);

        /* No morph, no problem */
        if (capability == null || !capability.isMorphed())
        {
            return;
        }

        AbstractMorph morph = capability.getCurrentMorph();
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        boolean inGUI = screen instanceof GuiInventory || screen instanceof GuiContainerCreative;

        event.setCanceled(true);

        /* Downscaling the player in GUIs */
        if (inGUI)
        {
            float scale = 1;
            float height = morph.getHeight(player);

            if (height > 2F)
            {
                // TODO: make a method for the setting up render scale
                scale = 1.3F / height;
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
        }

        /* Render the morph itself */
        morph.render(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());

        if (inGUI)
        {
            GlStateManager.popMatrix();
        }
    }
}