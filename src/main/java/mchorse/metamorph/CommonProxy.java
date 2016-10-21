package mchorse.metamorph;

import mchorse.metamorph.api.ModelHandler;
import mchorse.metamorph.api.morph.MorphHandler;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.metamorph.network.Dispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Common proxy
 * 
 * This proxy is responsible for number of things. For registering network 
 * messages, event handlers and capabilities. It also responsible for loading 
 * models.
 */
public class CommonProxy
{
    /**
     * Model handler. This class is responsible for managing models and more. 
     */
    public ModelHandler models = new ModelHandler();

    public void preLoad()
    {
        Dispatcher.register();
        EntityRegistry.registerModEntity(EntityMorph.class, "Morph", 0, Metamorph.instance, 64, 3, false);

        this.loadModels();
    }

    public void load()
    {
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new MorphHandler());

        MorphManager.INSTANCE.register();
        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing.class);
    }

    /**
     * Load mod's provided vanilla models 
     */
    public void loadModels()
    {
        /* Animals */
        this.loadModel("Bat");
        this.loadModel("Chicken");
        this.loadModel("Cow");
        this.loadModel("EntityHorse", "horse");
        this.loadModel("MushroomCow", "mooshroom");
        this.loadModel("Ozelot", "ocelot");
        this.loadModel("Pig");
        this.loadModel("PolarBear", "polar_bear");
        this.loadModel("Rabbit");
        this.loadModel("Sheep");
        this.loadModel("Squid");
        this.loadModel("Wolf");

        /* Neutral mobs */
        this.loadModel("Enderman");
        this.loadModel("PigZombie", "zombie_pigman");
        this.loadModel("SnowMan", "snow_man");
        this.loadModel("Villager");
        this.loadModel("VillagerGolem", "iron_golem");

        /* Hostile mobs */
        this.loadModel("Blaze");
        this.loadModel("CaveSpider", "cave_spider");
        this.loadModel("Creeper");
        this.loadModel("Ghast");
        this.loadModel("Guardian");
        this.loadModel("LavaSlime", "magma_cube");
        this.loadModel("Silverfish");
        this.loadModel("Skeleton");
        this.loadModel("Slime");
        this.loadModel("Spider");
        this.loadModel("Witch");
        this.loadModel("WitherSkeleton", "wither_skeleton");
        this.loadModel("Zombie");
    }

    /**
     * Load model with name 
     */
    private void loadModel(String model)
    {
        this.loadModel(model, model.toLowerCase());
    }

    /**
     * Load model with name and filename 
     */
    private void loadModel(String model, String filename)
    {
        try
        {
            this.models.load(model, filename);
        }
        catch (Exception e)
        {
            System.out.println("An exception was raised when loading '" + model + "' model!");
            e.printStackTrace();
        }
    }

    /**
     * Checks if the player is side's own
     * 
     * This method is responsible for determining to who player belongs. On 
     * server side it will always return true.
     */
    public boolean isOwnPlayer(EntityPlayer player)
    {
        return true;
    }
}