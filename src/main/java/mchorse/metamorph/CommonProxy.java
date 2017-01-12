package mchorse.metamorph;

import java.io.File;

import mchorse.metamorph.api.MorphHandler;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.models.ModelManager;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.config.MetamorphConfig;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.vanilla_pack.MobMorphFactory;
import mchorse.vanilla_pack.VanillaMorphFactory;
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
    public ModelManager models = new ModelManager();

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

    public void preLoad(FMLPreInitializationEvent event)
    {
        /* Network messages */
        Dispatcher.register();

        /* Attaching model manager and morph factories to the morph manager */
        MorphManager.INSTANCE.models = this.models;
        MorphManager.INSTANCE.factories.add(new MobMorphFactory());
        MorphManager.INSTANCE.factories.add(new VanillaMorphFactory());

        /* Configuration */
        File config = new File(event.getModConfigurationDirectory(), "metamorph/config.cfg");
        File morphs = new File(event.getModConfigurationDirectory(), "metamorph/morphs.json");

        this.forge = new Configuration(config);
        this.config = new MetamorphConfig(this.forge);
        this.morphs = morphs;

        /* Entities */
        EntityRegistry.registerModEntity(EntityMorph.class, "Morph", 0, Metamorph.instance, 64, 3, false);
    }

    /**
     * Load stuff
     * 
     * Add event listeners, register morphing capability and also load user 
     * configuration. I don't know how it's going to work in multiplayer, 
     * probably won't lol
     */
    public void load()
    {
        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(this.config);
        MinecraftForge.EVENT_BUS.register(new MorphHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

        /* Morphing manager and capabilities */
        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing.class);

        /* Register morph factories */
        MorphManager.INSTANCE.register();

        if (morphs.exists())
        {
            MorphUtils.loadMorphSettings(MorphManager.INSTANCE, morphs);
        }
        else
        {
            MorphUtils.generateEmptyMorphs(morphs);
        }
    }
}