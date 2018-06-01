package mchorse.metamorph.client;

import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiOverlay;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
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
    private GuiSurvivalMorphs overlay;
    private GuiOverlay morphOverlay;
    private RenderManager manager;

    public RenderingHandler(GuiSurvivalMorphs overlay, GuiOverlay morphOverlay)
    {
        this.overlay = overlay;
        this.morphOverlay = morphOverlay;
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

            GlStateManager.pushMatrix();

            if (capability.getCurrentMorph() == null && animation <= 10)
            {
                float anim = (animation - partialTick) / 10.0F;
                float offset = 0;

                if (anim >= 0)
                {
                    offset = -anim * anim * 2F;
                }

                GlStateManager.translate(0, offset, 0);

                if (anim >= 0)
                {
                    GlStateManager.rotate(anim * -90.0F, 1, 0, 0);
                    GlStateManager.scale(1 - anim, 1 - anim, 1 - anim);
                }
            }
            else if (capability.getPreviousMorph() == null && animation > 10)
            {
                float anim = (animation - 10 - partialTick) / 10.0F;
                float offset = 0;

                if (anim >= 0)
                {
                    offset = (1 - anim);
                }

                GlStateManager.translate(0, offset, 0);

                if (anim >= 0)
                {
                    GlStateManager.rotate((1 - anim) * 90.0F, 1, 0, 0);
                    GlStateManager.scale(anim, anim, anim);
                }
            }
        }
    }

    /**
     * Pop the matrix if animation is running 
     */
    @SubscribeEvent
    public void onPlayerPostRender(RenderPlayerEvent.Post event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing capability = Morphing.get(player);

        /* No morph, no problem */
        if (capability == null)
        {
            return;
        }

        if (capability.isAnimating())
        {
            GlStateManager.popMatrix();
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

        if (dist < (double) (factor * factor))
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
}