package mchorse.metamorph;

import java.util.Map;

import mchorse.metamorph.api.Model;
import mchorse.metamorph.client.RenderingHandler;
import mchorse.metamorph.client.model.parsing.ModelParser;
import mchorse.metamorph.client.render.RenderPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void load()
    {
        super.load();

        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        RenderPlayer render = new RenderPlayer(manager, 0.5F);

        MinecraftForge.EVENT_BUS.register(new RenderingHandler(render));
    }

    @Override
    public void loadModels()
    {
        super.loadModels();

        for (Map.Entry<String, Model> model : this.models.models.entrySet())
        {
            ModelParser.parse(model.getKey(), model.getValue());
        }
    }
}