package mchorse.metamorph.client;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.client.gui.GuiMenu;
import mchorse.metamorph.client.render.RenderHands;
import mchorse.metamorph.client.render.RenderPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Rendering handler
 *
 * This handler is another handler in this mod that responsible for rendering.
 * Currently this handler only renders recording overlay
 */
@SideOnly(Side.CLIENT)
public class RenderingHandler
{
    private RenderPlayer render;
    private GuiMenu overlay;
    private RenderHands hand;

    public RenderingHandler(GuiMenu overlay, RenderPlayer render)
    {
        this.overlay = overlay;
        this.render = render;
        this.hand = new RenderHands(render);
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
            this.overlay.render(resolution.getScaledWidth(), resolution.getScaledHeight());
        }
    }

    /**
     * Render morphed hands in first person
     */
    @SubscribeEvent
    public void onHandRender(RenderSpecificHandEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability == null || !capability.isMorphed())
        {
            return;
        }

        if (event.getItemStack() == null && event.getHand() == EnumHand.MAIN_HAND)
        {
            this.hand.render(player, event);
        }
    }

    /**
     * Morph (render) player into custom model if player has its variables
     * related to morphing (model and skin)
     */
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability == null || !capability.isMorphed()) return;

        event.setCanceled(true);
        this.render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());
    }
}