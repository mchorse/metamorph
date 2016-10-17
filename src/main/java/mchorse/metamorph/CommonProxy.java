package mchorse.metamorph;

import mchorse.metamorph.api.ModelHandler;
import mchorse.metamorph.api.morph.MorphHandler;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.network.Dispatcher;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;

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
        this.models.load("Bat");
        this.models.load("Chicken");
        this.models.load("Cow");
        this.models.load("MushroomCow", "mooshroom");
        this.models.load("Ozelot", "ocelot");
        this.models.load("Pig");
        this.models.load("PolarBear", "polar_bear");
        this.models.load("Rabbit");
        this.models.load("Sheep");
        this.models.load("Squid");
        this.models.load("Wolf");

        /* Neutral mobs */
        this.models.load("Villager");
        this.models.load("PigZombie", "zombie_pigman");

        /* Hostile mobs */
        this.models.load("CaveSpider", "cave_spider");
        this.models.load("Creeper");
        this.models.load("Ghast");
        this.models.load("LavaSlime", "magma_cube");
        this.models.load("Slime");
        this.models.load("Spider");
        this.models.load("Zombie");
    }
}