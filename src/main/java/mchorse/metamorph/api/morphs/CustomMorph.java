package mchorse.metamorph.api.morphs;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Custom morph class
 * 
 * 
 */
public class CustomMorph extends AbstractMorph
{
    /**
     * Morph's model
     */
    public Model model;

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    @Override
    public void update(EntityPlayer player, IMorphing cap)
    {
        this.updateSize(player, cap);

        super.update(player, cap);
    }

    /**
     * Update size of the player based on the given morph.
     */
    public void updateSize(EntityPlayer player, IMorphing cap)
    {
        Model data = cap.getCurrentMorph().model;
        String key = player.isElytraFlying() ? "flying" : (player.isSneaking() ? "sneaking" : "standing");

        float[] pose = data.poses.get(key).size;

        this.updateSize(player, pose[0], pose[1]);
    }
}