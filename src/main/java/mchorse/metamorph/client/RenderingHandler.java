package mchorse.metamorph.client;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.overlays.GuiHud;
import mchorse.metamorph.client.gui.overlays.GuiOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
    private GuiOverlay morphOverlay;
    private GuiHud hud;
    private RenderManager manager;

    public RenderingHandler(GuiOverlay morphOverlay, GuiHud hud)
    {
        this.morphOverlay = morphOverlay;
        this.hud = hud;
        this.manager = Minecraft.getMinecraft().getRenderManager();
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
            this.morphOverlay.render(resolution.getScaledWidth(), resolution.getScaledHeight());
        }
    }

    /**
     * Draw replacement air bar for morphs that can't breathe on land
     */
    @SubscribeEvent
    public void onAirRenderPre(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.AIR || event.isCanceled())
        {
            return;
        }

        if (this.hud.renderSquidAir)
        {
            event.setCanceled(true);

            ScaledResolution resolution = event.getResolution();
            this.hud.renderSquidAir(resolution.getScaledWidth(), resolution.getScaledHeight());
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
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing capability = Morphing.get(player);

        /* No morph, no problem */
        if (capability == null)
        {
            return;
        }

        int animation = capability.getAnimation();

        /* Render the morph */
        if (capability.renderPlayer(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick()))
        {
            event.setCanceled(true);
        }
        else if (capability.isAnimating())
        {
            float partialTick = event.getPartialRenderTick();
            float anim = 0;

            if (capability.getCurrentMorph() == null && animation <= 10)
            {
                anim = 1 - (animation - partialTick) / 10.0F;
            }
            else if (capability.getPreviousMorph() == null && animation > 10)
            {
                anim = (animation - 10 - partialTick) / 10.0F;
            }

            GlStateManager.color(1, 1, 1, anim);
        }
    }

    /**
     * On name render, simply render the name of the user, instead of the name of 
     * the entity.
     */
    @SubscribeEvent
    public void onNameRender(RenderLivingEvent.Specials.Pre<EntityLivingBase> event)
    {
        EntityLivingBase host = EntityMorph.renderEntity;

        if (host == null)
        {
            return;
        }

        event.setCanceled(true);

        EntityLivingBase target = event.getEntity();

        if (!this.canRenderName(host))
        {
            return;
        }

        double dist = target.getDistanceSq(this.manager.renderViewEntity);
        float factor = target.isSneaking() ? 32.0F : 64.0F;

        if (dist < factor * factor)
        {
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderEntityName(target, host.getDisplayName().getFormattedText(), event.getX(), event.getY(), event.getZ());
        }
    }

    /**
     * Can render the morph's name 
     */
    protected boolean canRenderName(EntityLivingBase host)
    {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
        boolean flag = !host.isInvisibleToPlayer(entityplayersp);

        if (host != entityplayersp)
        {
            Team team = host.getTeam();
            Team team1 = entityplayersp.getTeam();

            if (team != null)
            {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible)
                {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isSameTeam(team1) && flag;
                    default:
                        return true;
                }
            }
        }

        if (!(host instanceof EntityPlayer))
        {
            flag = flag && host.hasCustomName();
        }

        return Minecraft.isGuiEnabled() && host != this.manager.renderViewEntity && flag && !host.isBeingRidden();
    }

    /**
     * Renders an entity's name above its head (copied and modified from 
     * {@link RenderLivingBase})
     */
    protected void renderEntityName(EntityLivingBase entity, String name, double x, double y, double z)
    {
        if (name.isEmpty())
        {
            return;
        }

        boolean sneaking = entity.isSneaking();
        boolean thirdFrontal = this.manager.options.thirdPersonView == 2;

        float px = this.manager.playerViewY;
        float py = this.manager.playerViewX;
        float pz = entity.height + 0.5F - (sneaking ? 0.25F : 0.0F);

        int i = "deadmau5".equals(name) ? -10 : 0;

        EntityRenderer.drawNameplate(this.manager.getFontRenderer(), name, (float) x, (float) y + pz, (float) z, i, px, py, thirdFrontal, sneaking);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiMainMenu)
        {
            MorphManager.INSTANCE.list.reset();
            ClientProxy.survivalScreen = null;
        }
    }
}