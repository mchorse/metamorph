package mchorse.metamorph;

import java.io.File;

import mchorse.metamorph.api.ModelHandler;
import mchorse.metamorph.api.morph.MorphHandler;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.config.MetamorphConfig;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.metamorph.network.Dispatcher;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

    /**
     * Config
     */
    public MetamorphConfig config;

    /**
     * Forge config
     */
    public Configuration forge;

    public void preLoad(FMLPreInitializationEvent event)
    {
        /* Network messages */
        Dispatcher.register();

        /* Config */
        File config = new File(event.getModConfigurationDirectory(), "metamorph/config.cfg");

        this.forge = new Configuration(config);
        this.config = new MetamorphConfig(this.forge);

        MinecraftForge.EVENT_BUS.register(this.config);

        /* Entities */
        EntityRegistry.registerModEntity(EntityMorph.class, "Morph", 0, Metamorph.instance, 64, 3, false);

        this.loadModels();
    }

    public void load()
    {
        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new MorphHandler());

        /* Morphing manager and capabilities */
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
     * Get morph name 
     */
    public ITextComponent morphName(EntityMorph entityMorph)
    {
        return new TextComponentTranslation("entity." + entityMorph.morph + ".name");
    }
}