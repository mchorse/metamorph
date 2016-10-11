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

    public void loadModels()
    {
        this.models.loadModels();
    }
}