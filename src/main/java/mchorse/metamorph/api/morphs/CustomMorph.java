package mchorse.metamorph.api.morphs;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

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
}