package mchorse.metamorph.client;

import org.lwjgl.input.Keyboard;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.GuiMenu;
import mchorse.metamorph.client.gui.GuiMorphs;
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
    private KeyBinding keyMenu;

    /* Morph related keys */
    private KeyBinding keyNextMorph;
    private KeyBinding keyPrevMorph;
    private KeyBinding keySelectMorph;
    private KeyBinding keyDemorph;

    /* Survival morphing menu */
    private GuiMenu overlay;

    public KeyboardHandler()
    {
        String category = "key.metamorph";

        /* Create key bindings */
        keyAction = new KeyBinding("key.metamorph.action", Keyboard.KEY_V, category);
        keyMenu = new KeyBinding("key.metamorph.menu", Keyboard.KEY_B, category);

        keyNextMorph = new KeyBinding("key.metamorph.morph.next", Keyboard.KEY_RBRACKET, category);
        keyPrevMorph = new KeyBinding("key.metamorph.morph.prev", Keyboard.KEY_LBRACKET, category);
        keySelectMorph = new KeyBinding("key.metamorph.morph.select", Keyboard.KEY_RETURN, category);
        keyDemorph = new KeyBinding("key.metamorph.morph.demorph", Keyboard.KEY_PERIOD, category);

        /* Register them in the client registry */
        ClientRegistry.registerKeyBinding(keyAction);
        ClientRegistry.registerKeyBinding(keyMenu);

        ClientRegistry.registerKeyBinding(keyNextMorph);
        ClientRegistry.registerKeyBinding(keyPrevMorph);
        ClientRegistry.registerKeyBinding(keySelectMorph);
        ClientRegistry.registerKeyBinding(keyDemorph);
    }

    public KeyboardHandler(GuiMenu overlay)
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

        if (keyMenu.isPressed() && mc.thePlayer.isCreative())
        {
            mc.displayGuiScreen(new GuiMorphs());
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

        /* Apply selected morph */
        if (keySelectMorph.isPressed())
        {
            IMorphing morphing = Morphing.get(mc.thePlayer);

            /* Checking if we're morphing in the same thing */
            boolean isSame = false;
            boolean morphed = morphing.isMorphed();

            if (this.overlay.index == -1)
            {
                isSame = !morphed;
            }

            if (this.overlay.index >= 0 && morphed)
            {
                isSame = morphing.getCurrentMorph().equals(morphing.getAcquiredMorphs().get(this.overlay.index));
            }

            /* No need to send packet if it's the same */
            if (!isSame)
            {
                Dispatcher.sendToServer(new PacketSelectMorph(this.overlay.index));
                this.overlay.timer = 0;
            }
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