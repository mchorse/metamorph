package mchorse.metamorph.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import mchorse.mclib.utils.JsonUtils;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.capabilities.render.EntitySelector;
import mchorse.metamorph.capabilities.render.EntitySelectorAdapter;
import mchorse.metamorph.capabilities.render.IModelRenderer;
import mchorse.metamorph.capabilities.render.ModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity model handler. This handler is responsible for rendering 
 * models on player.
 */
@SideOnly(Side.CLIENT)
public class EntityModelHandler
{
    /**
     * Entity selectors
     */
    public static final List<EntitySelector> selectors = new ArrayList<EntitySelector>();

    /**
     * Entity selector GSON 
     */
    public Gson entitySelector;

    public Entity currentRendering;

    public EntityModelHandler()
    {
        /* Create GSON builder */
        GsonBuilder gson = new GsonBuilder();

        gson.registerTypeAdapter(EntitySelector.class, new EntitySelectorAdapter());
        gson.setPrettyPrinting();

        this.entitySelector = gson.create();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event)
    {
        EntityLivingBase entity = event.getEntity();
        IModelRenderer cap = ModelRenderer.get(entity);

        if (cap != null && currentRendering == null)
        {
            currentRendering = entity;

            if (cap.render(entity, event.getX(), event.getY(), event.getZ(), Minecraft.getMinecraft().getRenderPartialTicks()))
            {
                event.setCanceled(true);
            }

            currentRendering = null;
        }
    }

    @SubscribeEvent
    public void onUpdateEntity(LivingUpdateEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        IModelRenderer cap = ModelRenderer.get(entity);

        if (cap != null && entity.world.isRemote)
        {
            cap.update(entity);
        }
    }

    public void loadSelectors()
    {
        File selectorsFile = Metamorph.proxy.selectors;

        if (selectorsFile.exists())
        {
            try
            {
                Type token = (new TypeToken<List<EntitySelector>>() {}).getType();
                List<EntitySelector> selectors = this.entitySelector.fromJson(FileUtils.readFileToString(selectorsFile, Charset.defaultCharset()), token);

                EntityModelHandler.selectors.clear();
                EntityModelHandler.selectors.addAll(selectors);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveSelectors()
    {
        try
        {
            JsonElement element = this.entitySelector.toJsonTree(selectors);

            FileUtils.writeStringToFile(Metamorph.proxy.selectors, JsonUtils.jsonToPretty(element), Charset.defaultCharset());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}