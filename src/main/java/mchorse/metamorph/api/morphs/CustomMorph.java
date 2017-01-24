package mchorse.metamorph.api.morphs;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.models.Model.Pose;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.render.RenderCustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom morph class
 * 
 * This morph subclass is responsible for updating custom model based morphs
 */
public class CustomMorph extends AbstractMorph
{
    /**
     * Morph's model
     */
    public Model model;

    /**
     * Current pose 
     */
    public Pose pose;

    /* Rendering */

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        ModelCustom model = ModelCustom.MODELS.get(this.name);

        if (model != null)
        {
            Model data = model.model;

            if (data != null && data.defaultTexture != null)
            {
                model.pose = model.model.poses.get("standing");
                model.swingProgress = 0;

                Minecraft.getMinecraft().renderEngine.bindTexture(data.defaultTexture);
                GuiUtils.drawModel(model, player, x, y, scale, alpha);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        if (this.renderer == null || !(this.renderer instanceof RenderCustomModel))
        {
            return false;
        }

        RenderCustomModel renderer = (RenderCustomModel) this.renderer;

        renderer.setupModel(player);

        if (renderer.getMainModel() == null)
        {
            return false;
        }

        if (hand.equals(EnumHand.MAIN_HAND))
        {
            renderer.renderRightArm(player);
        }
        else
        {
            renderer.renderLeftArm(player);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        RenderCustomModel render = (RenderCustomModel) this.renderer;

        render.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /* Updating and stuff */

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        this.updateSize(target, cap);

        super.update(target, cap);
    }

    /**
     * Update size of the player based on the given morph.
     */
    public void updateSize(EntityLivingBase target, IMorphing cap)
    {
        this.pose = model.getPose(EntityUtils.getPose(target));

        if (this.pose != null)
        {
            float[] pose = this.pose.size;

            this.updateSize(target, pose[0], pose[1]);
        }
    }

    /**
     * Clone this {@link CustomMorph} 
     */
    @Override
    public AbstractMorph clone()
    {
        CustomMorph morph = new CustomMorph();

        morph.name = this.name;

        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.model = this.model;
        morph.renderer = this.renderer;

        return morph;
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return this.pose != null ? this.pose.size[0] : 0.6F;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return this.pose != null ? this.pose.size[1] : 1.8F;
    }
}