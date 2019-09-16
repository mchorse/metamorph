package mchorse.metamorph.client;

import org.lwjgl.input.Keyboard;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.GuiCreativeMenu;
import mchorse.metamorph.client.gui.GuiSurvivalMenu;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs;
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
    public KeyBinding keyNextMorph;
    public KeyBinding keyPrevMorph;
    public KeyBinding keyNextVarMorph;
    public KeyBinding keyPrevVarMorph;
    public KeyBinding keySelectMorph;
    public KeyBinding keyDemorph;

    public KeyboardHandler()
    {
        String category = "key.metamorph";

        /* Create key bindings */
        keyAction = new KeyBinding("key.metamorph.action", Keyboard.KEY_V, category);
        keyCreativeMenu = new KeyBinding("key.metamorph.creative_menu", Keyboard.KEY_B, category);
        keySurvivalMenu = new KeyBinding("key.metamorph.survival_menu", Keyboard.KEY_N, category);

        keyNextMorph = new KeyBinding("key.metamorph.morph.next", Keyboard.KEY_RBRACKET, category);
        keyPrevMorph = new KeyBinding("key.metamorph.morph.prev", Keyboard.KEY_LBRACKET, category);
        keyNextVarMorph = new KeyBinding("key.metamorph.morph.next_var", Keyboard.KEY_BACKSLASH, category);
        keyPrevVarMorph = new KeyBinding("key.metamorph.morph.prev_var", Keyboard.KEY_APOSTROPHE, category);
        keySelectMorph = new KeyBinding("key.metamorph.morph.select", Keyboard.KEY_RETURN, category);
        keyDemorph = new KeyBinding("key.metamorph.morph.demorph", Keyboard.KEY_PERIOD, category);

        /* Register them in the client registry */
        ClientRegistry.registerKeyBinding(keyAction);
        ClientRegistry.registerKeyBinding(keyCreativeMenu);
        ClientRegistry.registerKeyBinding(keySurvivalMenu);

        ClientRegistry.registerKeyBinding(keyNextMorph);
        ClientRegistry.registerKeyBinding(keyPrevMorph);
        ClientRegistry.registerKeyBinding(keyNextVarMorph);
        ClientRegistry.registerKeyBinding(keyPrevVarMorph);
        ClientRegistry.registerKeyBinding(keySelectMorph);
        ClientRegistry.registerKeyBinding(keyDemorph);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        final Minecraft mc = Minecraft.getMinecraft();

        EntityPlayer player = Minecraft.getMinecraft().player;
        IMorphing morphing = Morphing.get(player);
        GuiSurvivalMorphs overlay = morphing == null ? null : morphing.getOverlay();

        /* Action */
        if (keyAction.isPressed())
        {
            Dispatcher.sendToServer(new PacketAction());

            if (morphing != null && morphing.isMorphed())
            {
                morphing.getCurrentMorph().action(player);
            }
        }

        if (keyCreativeMenu.isPressed() && mc.player.isCreative())
        {
            mc.displayGuiScreen(new GuiCreativeMenu());
        }

        if (ClientProxy.getGameMode(mc.player) == GameType.ADVENTURE)
        {
            return;
        }

        /* Survival morphing key handling */
        if (keySurvivalMenu.isPressed() && overlay != null)
        {
            mc.displayGuiScreen(new GuiSurvivalMenu(overlay));
        }

        boolean prev = keyPrevMorph.isPressed();
        boolean next = keyNextMorph.isPressed();

        /* Selecting a morph */
        if ((prev || next) && overlay != null)
        {
            int factor = prev ? -1 : 1;

            /* If any of alts is pressed, then skip to the end or beginning */
            if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_LMENU))
            {
                overlay.skip(factor);
            }
            /* Then advance one or two indices forward or backward */
            else
            {
                int skip = factor * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? 2 : 1);

                overlay.advance(skip);
            }
        }

        if (keyNextVarMorph.isPressed() && overlay != null)
        {
            overlay.up();
        }
        else if (keyPrevVarMorph.isPressed() && overlay != null)
        {
            overlay.down();
        }

        /* Apply selected morph */
        if (keySelectMorph.isPressed() && overlay != null)
        {
            overlay.selectCurrent();
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