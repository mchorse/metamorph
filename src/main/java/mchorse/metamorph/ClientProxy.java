package mchorse.metamorph;

import java.util.Map;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.client.KeyboardHandler;
import mchorse.metamorph.client.RenderingHandler;
import mchorse.metamorph.client.gui.GuiMorphOverlay;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.model.parsing.ModelParser;
import mchorse.metamorph.client.render.RenderPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.common.MinecraftForge;

/**
 * Client proxy
 * 
 * Client proxy is responsible for adding some rendering modifications (i.e. 
 * HUD morph panel and player rendering) and also responsible for loading 
 * (constructing ModelCustom out of) custom models. 
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void load()
    {
        super.load();

        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        RenderPlayer render = new RenderPlayer(manager, 0.5F);
        GuiMorphOverlay overlay = new GuiMorphOverlay();

        MinecraftForge.EVENT_BUS.register(new RenderingHandler(overlay, render));
        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
    }

    @Override
    public void loadModels()
    {
        super.loadModels();

        for (Map.Entry<String, Model> model : this.models.models.entrySet())
        {
            Model data = model.getValue();

            if (data.model.isEmpty())
            {
                /* Parse default type of model */
                ModelParser.parse(model.getKey(), data);
            }
            else
            {
                try
                {
                    /* Parse custom custom (overcustomized) model */
                    ModelParser.parse(model.getKey(), data, (Class<? extends ModelCustom>) Class.forName(data.model));
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}