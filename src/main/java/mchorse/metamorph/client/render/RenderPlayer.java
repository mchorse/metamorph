package mchorse.metamorph.client.render;

import java.util.Map;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.model.ModelCustomRenderer;
import mchorse.metamorph.client.render.layers.LayerHeldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Player renderer
 *
 * Renders player entities with swag
 */
@SideOnly(Side.CLIENT)
public class RenderPlayer extends RenderLivingBase<EntityPlayer>
{
    public RenderPlayer(RenderManager renderManagerIn, float shadowSize)
    {
        super(renderManagerIn, null, shadowSize);

        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlayer entity)
    {
        return this.mainModel == null ? null : ((ModelCustom) this.mainModel).model.defaultTexture;
    }

    @Override
    public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.setupModel(entity);

        if (this.mainModel == null) return;

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Setup the model for player instance.
     *
     * This method is responsible for picking the right model and pose based
     * on player properties.
     */
    public void setupModel(EntityPlayer entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;
        IMorphing capability = entity.getCapability(MorphingProvider.MORPHING_CAP, null);

        String key = capability.getCurrentMorphName();
        String pose = entity.isSneaking() ? "sneaking" : (entity.isElytraFlying() ? "flying" : "standing");

        ModelCustom model = models.get(key);

        if (model != null)
        {
            model.pose = model.model.poses.get(pose);
            this.mainModel = model;
        }
    }

    /**
     * Make player a little bit smaller (so he looked like steve, and not like an 
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityPlayer entitylivingbaseIn, float partialTickTime)
    {
        Model model = ((ModelCustom) this.mainModel).model;

        GlStateManager.scale(model.scale[0], model.scale[1], model.scale[2]);
    }

    /**
     * Taken from RenderPlayer
     *
     * This code is primarily changes the angle of the player while it's flying
     * an elytra. You know,
     */
    @Override
    protected void rotateCorpse(EntityPlayer player, float pitch, float yaw, float partialTicks)
    {
        if (player.isEntityAlive() && player.isPlayerSleeping())
        {
            /* Nap time! */
            GlStateManager.rotate(player.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(player), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else if (player.isElytraFlying())
        {
            /* Elytra rotation */
            super.rotateCorpse(player, pitch, yaw, partialTicks);

            float f = player.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);

            Vec3d vec3d = player.getLook(partialTicks);

            double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            GlStateManager.rotate(f1 * (-90.0F - player.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (player.motionX * vec3d.xCoord + player.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = player.motionX * vec3d.zCoord - player.motionZ * vec3d.xCoord;

                GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
        else
        {
            super.rotateCorpse(player, pitch, yaw, partialTicks);
        }
    }

    /**
     * Render right hand 
     */
    public void renderRightArm(EntityPlayer player)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(this.getEntityTexture(player));
        this.mainModel.swingProgress = 0.0F;
        this.mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        for (ModelCustomRenderer arm : ((ModelCustom) this.mainModel).right)
        {
            arm.rotateAngleX = 0;
            arm.rotationPointX = -6 + (arm.limb.size[0] * 0.5F - arm.limb.size[0] * arm.limb.anchor[0]);
            arm.rotationPointY = 13 - (arm.limb.size[1] > 8 ? arm.limb.size[1] : arm.limb.size[1] + 2);
            arm.rotationPointZ = 0;
            arm.render(0.0625F);
        }

        GlStateManager.disableBlend();
    }

    /**
     * Render left hand 
     */
    public void renderLeftArm(EntityPlayer player)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(this.getEntityTexture(player));
        this.mainModel.swingProgress = 0.0F;
        this.mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        for (ModelCustomRenderer arm : ((ModelCustom) this.mainModel).left)
        {
            arm.rotateAngleX = 0;
            arm.rotationPointX = 8 + (arm.limb.size[0] * 0.5F - arm.limb.size[0] * arm.limb.anchor[0]);
            arm.rotationPointY = 13 - (arm.limb.size[1] > 8 ? arm.limb.size[1] : arm.limb.size[1]);
            arm.rotationPointZ = 0;
            arm.render(0.0625F);
        }

        GlStateManager.disableBlend();
    }
}