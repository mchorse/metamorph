package mchorse.metamorph;

import mchorse.metamorph.api.MorphHandler;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.RegisterHandler;
import mchorse.metamorph.capabilities.CapabilityHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingStorage;
import mchorse.metamorph.capabilities.render.IModelRenderer;
import mchorse.metamorph.capabilities.render.ModelRenderer;
import mchorse.metamorph.capabilities.render.ModelRendererStorage;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.metamorph.entity.SoundHandler;
import mchorse.metamorph.network.Dispatcher;
import mchorse.vanilla_pack.MetamorphFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.io.File;

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
     * Location of a user's morph settings
     */
    public File morphs;

    /**
     * Location of a user's morph blacklist
     */
    public File blacklist;

    /**
     * Location of a user's morph ID remapper
     */
    public File remap;

    /**
     * Location of a user's entity selectors (client side)
     */
    public File selectors;

    /**
     * Location of a user's custom global morph list (client side)
     */
    public File list;

    public void preLoad(FMLPreInitializationEvent event)
    {
        /* Network messages */
        Dispatcher.register();

        /* Attaching morph factory to the morph manager */
        MorphManager.INSTANCE.factories.add(new MetamorphFactory());

        /* Configuration */
        this.morphs = new File(event.getModConfigurationDirectory(), "metamorph/morphs.json");
        this.blacklist = new File(event.getModConfigurationDirectory(), "metamorph/blacklist.json");
        this.remap = new File(event.getModConfigurationDirectory(), "metamorph/remap.json");
        this.selectors = new File(event.getModConfigurationDirectory(), "metamorph/selectors.json");
        this.list = new File(event.getModConfigurationDirectory(), "metamorph/list.json");

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
    public void load()
    {
        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(new MorphHandler());
        MinecraftForge.EVENT_BUS.register(new SoundHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new RegisterHandler());

        /* Morphing manager and capabilities */
        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing::new);
        CapabilityManager.INSTANCE.register(IModelRenderer.class, new ModelRendererStorage(), ModelRenderer::new);

        /* Register morph factories */
        MorphManager.INSTANCE.register();

        /* User configuration */
        if (!this.morphs.exists())
        {
            MorphUtils.generateFile(this.morphs, "{}");
        }

        if (!this.blacklist.exists())
        {
            MorphUtils.generateFile(this.blacklist, "[]");
        }

        if (!this.remap.exists())
        {
            MorphUtils.generateFile(this.remap, "{}");
        }
    }

    /**
     * Post load
     */
    public void postLoad(FMLPostInitializationEvent event)
    {}

    public boolean canUse(EntityPlayer player)
    {
        return player.isCreative() || Metamorph.allowMorphingIntoCategoryMorphs.get();
    }
}