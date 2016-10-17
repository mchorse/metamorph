package mchorse.metamorph.client;

import org.lwjgl.input.Keyboard;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.client.gui.GuiMenu;
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
 * executing an action and morphing the player using "special menu."
 */
public class KeyboardHandler
{
    /* Action key */
    private KeyBinding keyAction;

    /* Morph related keys */
    private KeyBinding keyNextMorph;
    private KeyBinding keyPrevMorph;
    private KeyBinding keySelectMorph;

    private GuiMenu overlay;

    public KeyboardHandler()
    {
        String category = "key.metamorph";

        keyAction = new KeyBinding("key.metamorph.action", Keyboard.KEY_V, category);

        keyNextMorph = new KeyBinding("key.metamorph.morph.next", Keyboard.KEY_RBRACKET, category);
        keyPrevMorph = new KeyBinding("key.metamorph.morph.prev", Keyboard.KEY_LBRACKET, category);
        keySelectMorph = new KeyBinding("key.metamorph.morph.select", Keyboard.KEY_RETURN, category);

        ClientRegistry.registerKeyBinding(keyAction);

        ClientRegistry.registerKeyBinding(keyNextMorph);
        ClientRegistry.registerKeyBinding(keyPrevMorph);
        ClientRegistry.registerKeyBinding(keySelectMorph);
    }

    public KeyboardHandler(GuiMenu overlay)
    {
        this();
        this.overlay = overlay;
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        /* Action */
        if (keyAction.isPressed())
        {
            Dispatcher.sendToServer(new PacketAction());

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

            if (capability != null & capability.isMorphed())
            {
                capability.getCurrentMorph().action(player);
            }
        }

        /* Morphing */
        if (keyPrevMorph.isPressed())
        {
            this.overlay.prev();
        }
        else if (keyNextMorph.isPressed())
        {
            this.overlay.next();
        }
        else if (keySelectMorph.isPressed())
        {
            Dispatcher.sendToServer(new PacketSelectMorph(this.overlay.index));
        }
    }
}