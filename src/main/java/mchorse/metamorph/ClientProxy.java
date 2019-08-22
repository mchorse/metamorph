package mchorse.metamorph;

import java.lang.reflect.Field;
import java.util.Map;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.client.KeyboardHandler;
import mchorse.metamorph.client.NetworkHandler;
import mchorse.metamorph.client.RenderingHandler;
import mchorse.metamorph.client.gui.elements.GuiHud;
import mchorse.metamorph.client.gui.elements.GuiOverlay;
import mchorse.metamorph.client.render.RenderMorph;
import mchorse.metamorph.entity.EntityMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSubPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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
     * GUI overlay which is responsible for showing up acquired morphs
     */
    public static GuiOverlay morphOverlay = new GuiOverlay();
    
    public static GuiHud hud = new GuiHud();

    /**
     * Keyboard handler 
     */
    public static KeyboardHandler keys;

    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        super.preLoad(event);

        /* Register entity renderers */
        RenderingRegistry.registerEntityRenderingHandler(EntityMorph.class, new RenderMorph.MorphFactory());

        /* Registering an event channel for custom payload */
        Metamorph.channel.register(new NetworkHandler());
    }

    @Override
    public void load()
    {
        /* Continue loading process */
        super.load();

        /* Register client event handlers */
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(morphOverlay, hud));
        MinecraftForge.EVENT_BUS.register(keys = new KeyboardHandler());

        /* Register client morph manager */
        MorphManager.INSTANCE.registerClient();
    }

    /**
     * In post load, we're going to substitute player renderers 
     */
    @Override
    public void postLoad(FMLPostInitializationEvent event)
    {
        super.postLoad(event);

        /* Rendering stuff */
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();

        this.substitutePlayerRenderers(manager);
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
            RenderPlayer slim = skins.get("slim");
            RenderPlayer def = skins.get("default");

            skins.put("slim", new RenderSubPlayer(manager, slim, true));
            skins.put("default", new RenderSubPlayer(manager, def, false));

            Metamorph.log("Skin map renderers were successfully replaced with Metamorph substitutes!");
        }
    }

    /**
     * Get game mode of a player 
     */
    public static GameType getGameMode(EntityPlayer player)
    {
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getGameProfile().getId());

        return networkplayerinfo != null ? networkplayerinfo.getGameType() : GameType.CREATIVE;
    }
}