package mchorse.metamorph.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.builder.GuiMorphBuilder;
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
    private GuiButtonElement<GuiButton> toggle;
    private GuiButtonElement<GuiButton> fromMorph;
    private GuiTextElement search;
    private GuiCreativeMorphs pane;
    private GuiMorphBuilder builder;

    private boolean builderMode;

    public GuiCreativeMenu()
    {
        this.builder = new GuiMorphBuilder(this);

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
        this.top = GuiButtonElement.button(mc, "^", (b) -> this.pane.scrollTo(0));
        this.toggle = GuiButtonElement.button(mc, I18n.format("metamorph.gui.builder"), (b) ->
        {
            this.builderMode = !this.builderMode;
            this.updateButton();
        });

        this.fromMorph = GuiButtonElement.button(mc, I18n.format("metamorph.gui.from_morph"), (b) ->
        {
            if (!this.builderMode && this.builder.fromBuilder(this.getMorph()))
            {
                this.builderMode = true;
                this.updateButton();
            }
        });

        this.search = new GuiTextElement(mc, (filter) -> this.pane.setFilter(filter));
        this.search.field.setFocused(true);

        this.morph.resizer().parent(this.area).set(0, 5, 60, 20).x(1, -200);
        this.acquire.resizer().relative(this.morph.resizer()).set(65, 0, 60, 20);
        this.close.resizer().relative(this.acquire.resizer()).set(65, 0, 60, 20);

        this.toggle.resizer().parent(this.area).set(0, 35, 55, 20).x(1, -35 - 75 - 55);
        this.fromMorph.resizer().relative(this.toggle.resizer()).set(60, 0, 70, 20);
        this.top.resizer().relative(this.fromMorph.resizer()).set(75, 0, 20, 20);

        this.search.resizer().parent(this.area).set(60, 35, 0, 20).w(1, -60 - 95 - 75);

        this.elements.add(this.morph, this.acquire, this.close, this.top, this.toggle, this.fromMorph, this.search);
    }

    private void updateButton()
    {
        this.top.toggleVisible();
        this.toggle.button.displayString = this.builderMode ? I18n.format("metamorph.gui.list") : I18n.format("metamorph.gui.builder");
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

            this.pane = new GuiCreativeMorphs(6, morphing.getCurrentMorph(), morphing);
        }

        super.initGui();

        this.pane.setHidden(false);
        this.pane.updateRect(10, 55, this.width - 20, this.height - 55);
        this.pane.scrollBy(0);

        this.pane.setPerRow((int) Math.ceil((this.width - 20) / 54.0F));
        this.pane.setFilter(this.search.field.getText());

        this.builder.update(145, 35, this.width - 155, this.height - 35);
    }

    /**
     * Get currently selected morph 
     */
    public AbstractMorph getMorph()
    {
        if (this.builderMode)
        {
            return this.builder.currentBuilder.getMorph();
        }
        else
        {
            MorphCell selected = this.pane.getSelected();

            if (selected != null)
            {
                return selected.current().morph;
            }
        }

        return null;
    }

    /**
     * Handle mouse input 
     * 
     * This method is probably one of important ones in this class. It's 
     * responsible for scrolling the morph area.
     */
    @Override
    public void handleMouseInput() throws IOException
    {
        this.pane.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.pane.setWorldAndResolution(mc, width, height);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.builderMode)
        {
            this.builder.mouseClicked(mouseX, mouseY, mouseButton);
        }
        else
        {
            this.search.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Shortcuts for scrolling the morph menu up and down with arrow keys. 
     * 
     * There are also shortcuts for getting in the end or beginning of the 
     * screen (left and right arrow keys).
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        if (this.builderMode)
        {
            this.builder.keyTyped(typedChar, keyCode);
        }
        else
        {
            super.keyTyped(typedChar, keyCode);

            if (keyCode == Keyboard.KEY_DOWN)
            {
                this.pane.scrollBy(30);
            }
            else if (keyCode == Keyboard.KEY_UP)
            {
                this.pane.scrollBy(-30);
            }
            else if (keyCode == Keyboard.KEY_LEFT)
            {
                this.pane.scrollTo(0);
            }
            else if (keyCode == Keyboard.KEY_RIGHT)
            {
                this.pane.scrollTo(this.pane.getHeight());
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.builderMode)
        {
            this.builder.mouseReleased(mouseX, mouseY, state);
        }
        else
        {
            super.mouseReleased(mouseX, mouseY, state);
        }
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
        Gui.drawRect(0, 0, this.width, 30, 0x88000000);
        this.drawString(fontRendererObj, I18n.format("metamorph.gui.creative_title"), 10, 11, 0xffffff);

        /* Draw the builder */
        if (this.builderMode)
        {
            this.builder.draw(mouseX, mouseY, partialTicks);

            AbstractMorph morph = this.builder.currentBuilder.getMorph();

            if (morph != null)
            {
                this.drawCenteredString(fontRendererObj, morph.name, 70, 53, 0x888888);

                morph.renderOnScreen(Minecraft.getMinecraft().thePlayer, 70, height - (int) (height / 2.6), 43, 1.0F);
            }
        }
        else
        {
            /* Draw creative morphs */
            this.fontRendererObj.drawStringWithShadow(I18n.format("metamorph.gui.search"), 10, 41, 0xffffff);
            this.pane.drawScreen(mouseX, mouseY, partialTicks);

            /* Draw currently selected morph */
            MorphCell morph = this.pane.getSelected();
            String selected = morph != null ? morph.current().name : I18n.format("metamorph.gui.no_morph");

            this.drawCenteredString(fontRendererObj, selected, this.width / 2, this.height - 30, 0xffffffff);

            if (morph != null)
            {
                this.drawCenteredString(fontRendererObj, morph.current().morph.name, this.width / 2, this.height - 19, 0x888888);
            }
        }

        /* Render buttons */
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}