package mchorse.metamorph;

import java.util.Map;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.client.KeyboardHandler;
import mchorse.metamorph.client.RenderingHandler;
import mchorse.metamorph.client.gui.builder.GuiMorphBuilder;
import mchorse.metamorph.client.gui.elements.GuiOverlay;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs;
import mchorse.metamorph.client.render.RenderCustomModel;
import mchorse.metamorph.client.render.RenderMorph;
import mchorse.metamorph.entity.EntityMorph;
import mchorse.vanilla_pack.client.gui.GuiNBTMorphBuilder;
import mchorse.vanilla_pack.client.gui.GuiPlayerMorphBuilder;
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
        /* Continue loading process */
        super.load();

        /* Register client event handlers */
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(overlay, morphOverlay));
        MinecraftForge.EVENT_BUS.register(keys = new KeyboardHandler(overlay));

        /* Register client morph manager */
        MorphManager.INSTANCE.registerClient();

        /* Register morph builders */
        GuiMorphBuilder.BUILDERS.put("nbt", new GuiNBTMorphBuilder());
        GuiMorphBuilder.BUILDERS.put("player", new GuiPlayerMorphBuilder());
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
        modelRenderer = new RenderCustomModel(manager, null, 0.5F);

        this.substitutePlayerRenderers(manager);
    }

    /**
     * Substitute default player renders to get the ability to render the
     * hand.
     */
    private void substitutePlayerRenderers(RenderManager manager)
    {
        Map<String, net.minecraft.client.renderer.entity.RenderPlayer> skins = manager.getSkinMap();

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