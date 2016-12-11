package mchorse.metamorph;

import java.io.File;

import mchorse.metamorph.api.ModelManager;
import mchorse.metamorph.api.MorphHandler;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.config.MetamorphConfig;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.vanilla_pack.VanillaPack;
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
    public ModelManager models = new ModelManager();

    /**
     * Metamorph config filled with cool configuration points
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

        /* Configuration */
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
        MinecraftForge.EVENT_BUS.register(new MorphHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

        /* Morphing manager and capabilities */
        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing.class);

        VanillaPack.register();
    }

    /**
     * Load mod's provided vanilla models 
     */
    public void loadModels()
    {
        VanillaPack.loadModels(this.models);
    }

    /**
     * Get morph name 
     */
    public ITextComponent morphName(EntityMorph entityMorph)
    {
        return new TextComponentTranslation("entity." + entityMorph.morph + ".name");
    }
}