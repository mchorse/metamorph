package mchorse.metamorph;

import java.io.File;

import mchorse.metamorph.api.MorphHandler;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.config.MetamorphConfig;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.vanilla_pack.MobMorphFactory;
import mchorse.vanilla_pack.PlayerMorphFactory;
import mchorse.vanilla_pack.RegisterHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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
     * Metamorph config filled with cool configuration points
     */
    public MetamorphConfig config;

    /**
     * Forge config
     */
    public Configuration forge;

    /**
     * Location of a user morph settings
     */
    public File morphs;

    /**
     * Location of a user morph blacklist 
     */
    public File blacklist;

    public void preLoad(FMLPreInitializationEvent event)
    {
        /* Network messages */
        Dispatcher.register();

        /* Attaching morph factories to the morph manager */
        MorphManager.INSTANCE.factories.add(new MobMorphFactory());
        MorphManager.INSTANCE.factories.add(new PlayerMorphFactory());

        /* Configuration */
        File config = new File(event.getModConfigurationDirectory(), "metamorph/config.cfg");
        File morphs = new File(event.getModConfigurationDirectory(), "metamorph/morphs.json");
        File blacklist = new File(event.getModConfigurationDirectory(), "metamorph/blacklist.json");

        this.forge = new Configuration(config);
        this.config = new MetamorphConfig(this.forge);
        this.morphs = morphs;
        this.blacklist = blacklist;

        /* Entities */
        EntityRegistry.registerModEntity(new ResourceLocation("metamorph:morph"), EntityMorph.class, "Morph", 0, Metamorph.instance, 64, 3, false);
    }

    /**
     * Load stuff
     * 
     * Add event listeners, register morphing capability and also load user 
     * configuration. I don't know how it's going to work in multiplayer, 
     * probably won't lol
     */
    @SuppressWarnings("deprecation")
	public void load()
    {
        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(this.config);
        MinecraftForge.EVENT_BUS.register(new MorphHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new RegisterHandler());

        /* Morphing manager and capabilities */
        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing.class);

        /* Register morph factories */
        RegisterHandler.registerAbilities(MorphManager.INSTANCE);
        MorphManager.INSTANCE.register();

        /* User configuration */
        if (!morphs.exists())
        {
            MorphUtils.generateFile(morphs, "{}");
        }

        if (!blacklist.exists())
        {
            MorphUtils.generateFile(blacklist, "[]");
        }
    }

    /**
     * Post load
     */
    public void postLoad(FMLPostInitializationEvent event)
    {}
}