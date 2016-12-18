package mchorse.metamorph.api.morphs;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.GuiUtils;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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
     * Alternative skin for this morph
     */
    public ResourceLocation skin;

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        ModelCustom model = ModelCustom.MODELS.get(this.name);
        Model data = model.model;

        model.pose = model.model.poses.get("standing");
        model.swingProgress = 0;

        Minecraft.getMinecraft().renderEngine.bindTexture(data.defaultTexture);
        GuiUtils.drawModel(model, player, x, y, scale, alpha);
    }

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
        String key = target.isElytraFlying() ? "flying" : (target.isSneaking() ? "sneaking" : "standing");
        float[] pose = model.poses.get(key).size;

        this.updateSize(target, pose[0], pose[1]);
    }

    /**
     * Clone this {@link CustomMorph} 
     */
    @Override
    public AbstractMorph clone()
    {
        CustomMorph morph = new CustomMorph();

        morph.name = this.name;
        morph.category = this.category;

        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.model = this.model;
        morph.skin = this.skin;
        morph.renderer = this.renderer;

        return morph;
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return model.poses.get("standing").size[0];
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return model.poses.get("standing").size[1];
    }

    /**
     * Add skin field to NBT when persisting 
     */
    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.skin != null)
        {
            tag.setString("Skin", this.skin.toString());
        }
    }

    /**
     * Read skin field from NBT
     */
    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Skin"))
        {
            this.skin = new ResourceLocation(tag.getString("Skin"));
        }
    }
}