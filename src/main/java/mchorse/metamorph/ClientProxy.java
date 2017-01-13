package mchorse.metamorph;

import java.lang.reflect.Field;
import java.util.Map;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.client.KeyboardHandler;
import mchorse.metamorph.client.RenderingHandler;
import mchorse.metamorph.client.gui.elements.GuiOverlay;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs;
import mchorse.metamorph.client.render.RenderCustomModel;
import mchorse.metamorph.client.render.RenderMorph;
import mchorse.metamorph.client.render.RenderSubPlayer;
import mchorse.metamorph.entity.EntityMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client proxy
 * 
 * Client proxy is responsible for adding some rendering modifications (i.e. 
 * HUD morph panel and player rendering) and also responsible for loading 
 * (constructing ModelCustom out of) custom models. 
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    /**
     * GUI menu which is responsible for choosing morphs 
     */
    public static GuiSurvivalMorphs overlay = new GuiSurvivalMorphs();

    /**
     * GUI overlay which is responsible for showing up acquired morphs
     */
    public static GuiOverlay morphOverlay = new GuiOverlay();

    /**
     * Custom model renderer 
     */
    public static RenderCustomModel modelRenderer;

    /**
     * Keyboard handler 
     */
    public static KeyboardHandler keys;

    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        super.preLoad(event);

        RenderingRegistry.registerEntityRenderingHandler(EntityMorph.class, new RenderMorph.MorphFactory());
    }

    @Override
    public void load()
    {
        /* Rendering stuff */
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        modelRenderer = new RenderCustomModel(manager, null, 0.5F);

        this.substitutePlayerRenderers(manager);

        /* Continue loading process */
        super.load();

        /* Register client event handlers */
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(overlay, morphOverlay));
        MinecraftForge.EVENT_BUS.register(keys = new KeyboardHandler(overlay));

        /* Register client morph manager */
        MorphManager.INSTANCE.registerClient();
    }

    /**
     * Substitute default player renders to get the ability to render the
     * hand.
     *
     * Please, kids, don't do that at home. This was made by an expert in
     * his field, so please, don't override skinMap the way I did. Don't break
     * the compatibility with this mod (already confirmed breaking while 
     * using Metamorph and Blockbuster together).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void substitutePlayerRenderers(RenderManager manager)
    {
        Map<String, net.minecraft.client.renderer.entity.RenderPlayer> skins = null;

        /* Iterate over all render manager fields and get access to skinMap */
        for (Field field : manager.getClass().getDeclaredFields())
        {
            if (field.getType().equals(Map.class))
            {
                field.setAccessible(true);

                try
                {
                    Map map = (Map) field.get(manager);

                    if (map.get("default") instanceof net.minecraft.client.renderer.entity.RenderPlayer)
                    {
                        skins = map;

                        break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        /* Replace player renderers with Blockbuster substitutes */
        if (skins != null)
        {
            skins.put("slim", new RenderSubPlayer(manager, true));
            skins.put("default", new RenderSubPlayer(manager, false));

            Metamorph.log("Skin map renderers were successfully replaced with Metamorph substitutes!");
        }
    }
}