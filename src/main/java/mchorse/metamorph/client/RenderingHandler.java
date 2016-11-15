package mchorse.metamorph.client;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.client.gui.GuiMenu;
import mchorse.metamorph.client.render.ItemRenderer;
import mchorse.metamorph.client.render.RenderPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
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
    private ItemRenderer item;

    public RenderingHandler(GuiMenu overlay, RenderPlayer render)
    {
        this.overlay = overlay;
        this.render = render;
        this.item = new ItemRenderer(Minecraft.getMinecraft(), render);
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

    @SubscribeEvent
    public void onHandRender(RenderHandEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        EntityRenderer renderer = mc.entityRenderer;

        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);
        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();

        this.render.setupModel(player);

        if (capability == null || !capability.isMorphed() || this.render.getMainModel() == null)
        {
            return;
        }

        if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())
        {
            event.setCanceled(true);

            renderer.enableLightmap();
            this.item.renderItemInFirstPerson(event.getPartialTicks());
            renderer.disableLightmap();
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            return;
        }

        this.item.updateEquippedItem();
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