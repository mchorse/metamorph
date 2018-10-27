package mchorse.metamorph.client.gui;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Creative morphs GUI
 * 
 * This class is responsible for allowing creative players to open up this GUI 
 * and select one of the available morphs in the game.
 * 
 * When player selects a morph and presses "Morph" button, he turns into this 
 * morphs, however, selected morph doesn't saves in player's acquired 
 * morphs.
 * 
 * Really cool menu.
 */
public class GuiCreativeMenu extends GuiBase
{
    /* GUI stuff */
    private GuiButtonElement<GuiButton> morph;
    private GuiButtonElement<GuiButton> acquire;
    private GuiButtonElement<GuiButton> close;
    private GuiButtonElement<GuiButton> top;
    private GuiButtonElement<GuiButton> edit;
    private GuiTextElement search;
    private GuiCreativeMorphs pane;

    private boolean editMode;

    public GuiCreativeMenu()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.morph = GuiButtonElement.button(mc, I18n.format("metamorph.gui.morph"), (b) ->
        {
            AbstractMorph morph = this.getMorph();

            if (morph != null)
            {
                Dispatcher.sendToServer(new PacketMorph(morph));
                this.closeScreen();
            }
        });

        this.acquire = GuiButtonElement.button(mc, I18n.format("metamorph.gui.acquire"), (b) ->
        {
            Dispatcher.sendToServer(new PacketAcquireMorph(this.getMorph()));
        });

        this.close = GuiButtonElement.button(mc, I18n.format("metamorph.gui.close"), (b) -> this.closeScreen());
        this.top = GuiButtonElement.button(mc, "^", (b) -> this.pane.scroll.scrollTo(0));
        this.edit = GuiButtonElement.button(mc, I18n.format("metamorph.gui.builder"), (b) ->
        {
            this.editMode = !this.editMode;
            this.updateButton();
        });

        this.search = new GuiTextElement(mc, (filter) -> this.pane.setFilter(filter));
        this.search.field.setFocused(true);

        this.morph.resizer().parent(this.area).set(0, 5, 60, 20).x(1, -200);
        this.acquire.resizer().relative(this.morph.resizer()).set(65, 0, 60, 20);
        this.close.resizer().relative(this.acquire.resizer()).set(65, 0, 60, 20);

        this.edit.resizer().parent(this.area).set(0, 35, 55, 20).x(1, -35 - 55);
        this.top.resizer().relative(this.edit.resizer()).set(60, 0, 20, 20);

        this.search.resizer().parent(this.area).set(60, 35, 0, 20).w(1, -60 - 95);

        this.elements.add(this.morph, this.acquire, this.close, this.top, this.edit, this.search);
    }

    private void updateButton()
    {
        this.top.toggleVisible();
        this.edit.button.displayString = this.editMode ? I18n.format("metamorph.gui.list") : I18n.format("metamorph.gui.builder");
    }

    /* GUI stuff and input */

    /**
     * Initiate GUI
     * 
     * Nothing really special, my other mod has like a ton of stuff over here, 
     * but here, there's nothing much.
     */
    @Override
    public void initGui()
    {
        if (this.pane == null)
        {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            IMorphing morphing = Morphing.get(player);

            this.pane = new GuiCreativeMorphs(this.mc, 6, morphing.getCurrentMorph(), morphing);
            this.pane.resizer().parent(this.area).set(10, 55, 0, 0).w(1, -20).h(1, -55);

            this.elements.add(this.pane);
        }

        super.initGui();

        this.pane.setVisible(true);
        this.pane.scroll.scrollBy(0);

        this.pane.setPerRow((int) Math.ceil((this.width - 20) / 54.0F));
        this.pane.setFilter(this.search.field.getText());
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
        this.drawString(fontRendererObj, I18n.format("metamorph.gui.creative_title"), 10, 11, 0xffffff);

        /* Render buttons */
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!this.editMode)
        {
            /* Draw creative morphs */
            this.fontRendererObj.drawStringWithShadow(I18n.format("metamorph.gui.search"), 10, 41, 0xffffff);

            /* Draw currently selected morph */
            MorphCell morph = this.pane.getSelected();
            String selected = morph != null ? morph.current().name : I18n.format("metamorph.gui.no_morph");

            this.drawCenteredString(fontRendererObj, selected, this.width / 2, this.height - 30, 0xffffffff);

            if (morph != null)
            {
                this.drawCenteredString(fontRendererObj, morph.current().morph.name, this.width / 2, this.height - 19, 0x888888);
            }
        }
    }
}