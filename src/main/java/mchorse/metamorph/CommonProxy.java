package mchorse.metamorph;

import mchorse.metamorph.api.ModelHandler;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.network.Dispatcher;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CommonProxy
{
    public ModelHandler models;

    public void preLoad()
    {
        Dispatcher.register();
    }

    public void load()
    {
        MinecraftForge.EVENT_BUS.register(this.models = new ModelHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing.class);

        this.loadModels();
    }

    public void loadModels()
    {
        this.models.loadModels();
    }
}