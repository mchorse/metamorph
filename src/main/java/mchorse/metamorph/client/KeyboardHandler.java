package mchorse.metamorph.client;

import org.lwjgl.input.Keyboard;

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

    /* Survival morphing menu */
    private GuiSurvivalMorphs overlay;

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

    public KeyboardHandler(GuiSurvivalMorphs overlay)
    {
        this();
        this.overlay = overlay;
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        final Minecraft mc = Minecraft.getMinecraft();

        /* Action */
        if (keyAction.isPressed())
        {
            Dispatcher.sendToServer(new PacketAction());

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            IMorphing capability = Morphing.get(player);

            if (capability != null & capability.isMorphed())
            {
                capability.getCurrentMorph().action(player);
            }
        }

        if (keyCreativeMenu.isPressed() && mc.thePlayer.isCreative())
        {
            mc.displayGuiScreen(new GuiCreativeMenu());
        }

        if (keySurvivalMenu.isPressed())
        {
            mc.displayGuiScreen(new GuiSurvivalMenu(this.overlay));
        }

        boolean prev = keyPrevMorph.isPressed();
        boolean next = keyNextMorph.isPressed();

        /* Selecting a morph */
        if (prev || next)
        {
            int factor = prev ? -1 : 1;

            /* If any of alts is pressed, then skip to the end or beginning */
            if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_LMENU))
            {
                this.overlay.skip(factor);
            }
            /* Then advance one or two indices forward or backward */
            else
            {
                int skip = factor * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? 2 : 1);

                this.overlay.advance(skip);
            }
        }

        if (keyNextVarMorph.isPressed())
        {
            this.overlay.up();
        }
        else if (keyPrevVarMorph.isPressed())
        {
            this.overlay.down();
        }

        /* Apply selected morph */
        if (keySelectMorph.isPressed())
        {
            this.overlay.selectCurrent();
        }

        /* Demorph from current morph */
        if (keyDemorph.isPressed())
        {
            IMorphing morph = Morphing.get(mc.thePlayer);

            if (morph != null && morph.isMorphed())
            {
                Dispatcher.sendToServer(new PacketSelectMorph(-1));
            }
        }
    }
}