package mchorse.metamorph.client.gui;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Creative morphs GUI
 * 
 * This class is responsible for allowing creative players to open up 
 * this GUI and select one of the available morphs in the game.
 * 
 * When player selects a morph and presses "Morph" button, he turns into 
 * this morphs, however, selected morph doesn't saves in player's 
 * acquired morphs.
 * 
 * This menu also allows player to edit morphs.
 * 
 * TODO: sync edited acquired morphs
 */
public class GuiCreativeMenu extends GuiBase
{
    /* GUI stuff */
    private GuiButtonElement<GuiButton> morph;
    private GuiButtonElement<GuiButton> acquire;
    private GuiButtonElement<GuiButton> close;
    private GuiCreativeMorphs pane;

    public GuiCreativeMenu()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.morph = GuiButtonElement.button(mc, I18n.format("metamorph.gui.morph"), (b) ->
        {
            this.pane.finish();
            AbstractMorph morph = this.getMorph();

            if (morph != null)
            {
                Dispatcher.sendToServer(new PacketMorph(morph));
                this.closeScreen();
            }
        });

        this.acquire = GuiButtonElement.button(mc, I18n.format("metamorph.gui.acquire"), (b) ->
        {
            this.pane.finish();
            Dispatcher.sendToServer(new PacketAcquireMorph(this.getMorph()));
        });

        this.close = GuiButtonElement.button(mc, I18n.format("metamorph.gui.close"), (b) -> this.closeScreen());

        this.morph.resizer().parent(this.area).set(0, 10, 60, 20).x(1, -200);
        this.acquire.resizer().relative(this.morph.resizer()).set(65, 0, 60, 20);
        this.close.resizer().relative(this.acquire.resizer()).set(65, 0, 60, 20);

        this.elements.add(this.morph, this.acquire, this.close);
    }

    /* GUI stuff and input */

    @Override
    public void initGui()
    {
        if (this.pane == null)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            IMorphing morphing = Morphing.get(player);

            /* Create pane after constructor, because new morphs may 
             * appear during open GUI event */
            this.pane = new GuiCreativeMorphs(this.mc, 6, morphing.getCurrentMorph(), morphing);
            this.pane.resizer().parent(this.area).set(0, 40, 0, 0).w(1, 0).h(1, -40);
            this.pane.shiftX = 9;

            this.elements.elements.add(0, this.pane);
        }

        super.initGui();

        this.pane.scroll.scrollBy(0);
        this.pane.setPerRow((int) Math.ceil((this.width - 20) / 50.0F));
    }

    /**
     * Get currently selected morph 
     */
    public AbstractMorph getMorph()
    {
        MorphCell selected = this.pane.getSelected();

        if (selected != null)
        {
            return selected.current().morph;
        }

        return null;
    }

    /**
     * Draw screen
     * 
     * This method is responsible for number of things. This method renders 
     * everything, starting from buttons, and ending with morphs.
     * 
     * Event though the GUI by itself looks pretty simple, it has a pretty big 
     * method for rendering everything.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        /* Draw panel backgrounds */
        this.drawDefaultBackground();
        Gui.drawRect(0, 0, this.width, 35, 0x88000000);
        this.drawGradientRect(0, 35, this.width, 45, 0x88000000, 0x00000000);

        /* Render buttons */
        super.drawScreen(mouseX, mouseY, partialTicks);

        /* Draw stats about currently selected morph */
        MorphCell morph = this.pane.getSelected();
        String selected = morph != null ? morph.current().name : I18n.format("metamorph.gui.no_morph");

        this.fontRendererObj.drawStringWithShadow(selected, 10, 12, 0xffffffff);

        if (morph != null)
        {
            this.fontRendererObj.drawStringWithShadow(morph.current().morph.name, 10, 22, 0x888888);
        }
    }
}