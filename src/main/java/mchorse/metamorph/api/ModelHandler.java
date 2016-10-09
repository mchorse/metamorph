package mchorse.metamorph.api;

import java.util.HashMap;
import java.util.Map;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * This class responsible for storing domain custom models and sending models to
 * players who are logged in.
 */
public class ModelHandler
{
    /**
     * Cached models, they're loaded from stuffs
     */
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Load default provided models into model map
     */
    public void loadModels()
    {
        try
        {
            String path = "assets/metamorph/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            this.models.put("Chicken", Model.parse(loader.getResourceAsStream(path + "chicken.json")));
            this.models.put("Sheep", Model.parse(loader.getResourceAsStream(path + "sheep.json")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * On player tick, we have to change AABB box (total rip-off of
     * EntityActor#updateSize method)
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START) return;

        EntityPlayer player = event.player;
        IMorphing cap = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (!cap.isMorphed())
        {
            /* Restore default eye height */
            player.eyeHeight = player.getDefaultEyeHeight();
            return;
        }

        Model data = cap.getCurrentMorph().model;
        String key = player.isElytraFlying() ? "flying" : (player.isSneaking() ? "sneaking" : "standing");

        System.out.println(cap.getCurrentMorphName() + " " + cap.getCurrentMorph());

        float[] pose = data.poses.get(key).size;
        float width = pose[0];
        float height = pose[1];

        /* This is a total rip-off of EntityPlayer#setSize method */
        if (width != player.width || height != player.height)
        {
            float f = player.width;
            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();

            player.width = width;
            player.height = height;
            player.eyeHeight = height * 0.9F;
            player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + width, axisalignedbb.minY + height, axisalignedbb.minZ + width));

            if (player.width > f && !player.worldObj.isRemote)
            {
                player.moveEntity(f - player.width, 0.0D, f - player.width);
            }
        }
    }
}