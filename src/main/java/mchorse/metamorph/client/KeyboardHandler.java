package mchorse.metamorph.client;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.creative.GuiCreativeScreen;
import mchorse.metamorph.client.gui.creative.GuiSelectorsScreen;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.survival.PacketAction;
import mchorse.metamorph.network.common.survival.PacketSelectMorph;
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
    private KeyBinding keySelectorMenu;
    public KeyBinding keySurvivalMenu;

    /* Morph related keys */
    public KeyBinding keyMorphRecent;
    public KeyBinding keyDemorph;

    public KeyboardHandler()
    {
        String category = "key.metamorph";

        /* Create key bindings */
        keyAction = new KeyBinding("key.metamorph.action", Keyboard.KEY_V, category);
        keyCreativeMenu = new KeyBinding("key.metamorph.creative_menu", Keyboard.KEY_B, category);
        keySelectorMenu = new KeyBinding("key.metamorph.selector_menu", Keyboard.KEY_MINUS, category);
        keySurvivalMenu = new KeyBinding("key.metamorph.survival_menu", Keyboard.KEY_X, category);

        keyMorphRecent = new KeyBinding("key.metamorph.morph_recent", Keyboard.KEY_RETURN, category);
        keyDemorph = new KeyBinding("key.metamorph.demorph", Keyboard.KEY_PERIOD, category);

        /* Register them in the client registry */
        ClientRegistry.registerKeyBinding(keyAction);
        ClientRegistry.registerKeyBinding(keyCreativeMenu);
        ClientRegistry.registerKeyBinding(keySelectorMenu);
        ClientRegistry.registerKeyBinding(keySurvivalMenu);

        ClientRegistry.registerKeyBinding(keyMorphRecent);
        ClientRegistry.registerKeyBinding(keyDemorph);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        IMorphing morphing = Morphing.get(player);

        boolean wasUsed = false;
        boolean spectator = player.isSpectator();

        if (this.keyCreativeMenu.isPressed() && Metamorph.proxy.canUse(player))
        {
            mc.displayGuiScreen(new GuiCreativeScreen(mc));
            wasUsed = true;
        }

        if (this.keySelectorMenu.isPressed())
        {
            mc.displayGuiScreen(new GuiSelectorsScreen(mc));
            wasUsed = true;
        }

        /* Action */
        if (this.keyAction.isPressed() && !spectator)
        {
            Dispatcher.sendToServer(new PacketAction());

            if (morphing != null && morphing.isMorphed())
            {
                morphing.getCurrentMorph().action(player);
                wasUsed = true;
            }
        }

        /* Survival morphing key handling */
        if (this.keySurvivalMenu.isPressed() && !spectator)
        {
            mc.displayGuiScreen(ClientProxy.getSurvivalScreen().open());
            wasUsed = true;
        }
        
        /* Morph into the most recent attempted morph */
        if (this.keyMorphRecent.isPressed() && !spectator)
        {
            if (morphing != null && morphing.getLastSelectedMorph() != null)
            {
                MorphAPI.selectMorph(morphing.getLastSelectedMorph());
                wasUsed = true;
            }
        }

        /* Demorph from current morph */
        if (this.keyDemorph.isPressed() && !spectator)
        {
            if (morphing != null && morphing.isMorphed())
            {
                MorphAPI.selectDemorph();
                wasUsed = true;
            }
        }

        if (!wasUsed && Keyboard.getEventKeyState())
        {
            int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            MorphManager.INSTANCE.list.keyTyped(player, key);
        }
    }
}