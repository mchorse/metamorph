package mchorse.metamorph.client;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.creative.GuiCreativeScreen;
import mchorse.metamorph.client.gui.survival.GuiSurvivalScreen;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAction;
import mchorse.metamorph.network.common.PacketSelectMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Keyboard handler 
 * 
 * This class (handler) is responsible for handling the keyboard input for 
 * executing an action and morphing the player using survival morphing menu.
 * 
 * This handler is also responsible for opening up creative morphing menu.
 */
public class KeyboardHandler
{
    /* Action key */
    private KeyBinding keyAction;
    private KeyBinding keyCreativeMenu;
    private KeyBinding keySurvivalMenu;

    /* Morph related keys */
    public KeyBinding keyDemorph;

    public KeyboardHandler()
    {
        String category = "key.metamorph";

        /* Create key bindings */
        keyAction = new KeyBinding("key.metamorph.action", Keyboard.KEY_V, category);
        keyCreativeMenu = new KeyBinding("key.metamorph.creative_menu", Keyboard.KEY_B, category);
        keySurvivalMenu = new KeyBinding("key.metamorph.survival_menu", Keyboard.KEY_X, category);

        keyDemorph = new KeyBinding("key.metamorph.morph.demorph", Keyboard.KEY_PERIOD, category);

        /* Register them in the client registry */
        ClientRegistry.registerKeyBinding(keyAction);
        ClientRegistry.registerKeyBinding(keyCreativeMenu);
        ClientRegistry.registerKeyBinding(keySurvivalMenu);

        ClientRegistry.registerKeyBinding(keyDemorph);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        EntityPlayer player = mc.player;
        IMorphing morphing = Morphing.get(player);

        /* Action */
        if (keyAction.isPressed())
        {
            Dispatcher.sendToServer(new PacketAction());

            if (morphing != null && morphing.isMorphed())
            {
                morphing.getCurrentMorph().action(player);
            }
        }

        if (keyCreativeMenu.isPressed() && player.isCreative())
        {
            mc.displayGuiScreen(new GuiCreativeScreen());
        }

        if (ClientProxy.getGameMode(player) == GameType.ADVENTURE)
        {
            return;
        }

        /* Survival morphing key handling */
        if (keySurvivalMenu.isPressed())
        {
            mc.displayGuiScreen(ClientProxy.getSurvivalScreen().open());
        }

        /* Demorph from current morph */
        if (keyDemorph.isPressed())
        {
            if (morphing != null && morphing.isMorphed())
            {
                Dispatcher.sendToServer(new PacketSelectMorph(-1));
            }
        }
    }
}