package mchorse.metamorph.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
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
public class GuiCreativeMenu extends GuiScreen
{
    /* GUI stuff */
    private GuiButton morph;
    private GuiButton acquire;
    private GuiButton close;
    private GuiButton top;
    private GuiButton toggle;
    private GuiButton fromMorph;
    private GuiTextField search;
    private GuiCreativeMorphs pane;
    private GuiMorphBuilder builder;

    private boolean builderMode;

    public GuiCreativeMenu()
    {
        this.builder = new GuiMorphBuilder(this);
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

        int x = width - 10;
        int y = 5;

        this.morph = new GuiButton(0, x - 190, y, 60, 20, I18n.format("metamorph.gui.morph"));
        this.acquire = new GuiButton(1, x - 125, y, 60, 20, I18n.format("metamorph.gui.acquire"));
        this.close = new GuiButton(2, x - 60, y, 60, 20, I18n.format("metamorph.gui.close"));
        this.top = new GuiButton(3, this.width - 30, 35, 20, 20, "^");
        this.top.visible = !this.builderMode;
        this.toggle = new GuiButton(4, 10, this.height - 30, 55, 20, I18n.format("metamorph.gui.builder"));
        this.fromMorph = new GuiButton(5, 145 - 75, this.height - 30, 65, 20, I18n.format("metamorph.gui.from_morph"));

        this.search = new GuiTextField(-1, this.fontRendererObj, 195 + 1, 35 + 1, this.width - 230 - 2, 20 - 2);
        this.search.setFocused(true);

        this.buttonList.add(this.morph);
        this.buttonList.add(this.acquire);
        this.buttonList.add(this.close);
        this.buttonList.add(this.top);
        this.buttonList.add(this.toggle);
        this.buttonList.add(this.fromMorph);

        this.pane.setHidden(false);
        this.pane.updateRect(145, 55, this.width - 155, this.height - 55);
        this.pane.scrollBy(0);

        this.pane.setPerRow((int) Math.ceil((this.width - 155) / 54.0F));
        this.pane.setFilter(this.search.getText());

        this.builder.update(145, 35, this.width - 155, this.height - 35);
    }

    /**
     * Action dispatcher method
     * 
     * This method is responsible for closing this GUI and optionally send a 
     * message to server with the morph.
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 3 && !this.builderMode)
        {
            this.pane.scrollTo(0);
        }
        else if (button.id == 4)
        {
            this.builderMode = !this.builderMode;
            this.top.visible = !this.builderMode;
            this.toggle.displayString = this.builderMode ? I18n.format("metamorph.gui.list") : I18n.format("metamorph.gui.builder");
        }
        else if (button.id == 5)
        {
            if (!this.builderMode && this.builder.fromBuilder(this.getMorph()))
            {
                this.builderMode = true;
                this.top.visible = !this.builderMode;
                this.toggle.displayString = this.builderMode ? I18n.format("metamorph.gui.list") : I18n.format("metamorph.gui.builder");
            }
        }
        else if (button.id <= 2)
        {
            AbstractMorph morph = this.getMorph();

            if (button.id != 1)
            {
                if (button.id == 0)
                {
                    Dispatcher.sendToServer(new PacketMorph(morph));
                }

                Minecraft.getMinecraft().displayGuiScreen(null);
            }
            else if (morph != null)
            {
                Dispatcher.sendToServer(new PacketAcquireMorph(morph));
            }
        }
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

            this.search.textboxKeyTyped(typedChar, keyCode);

            if (this.search.isFocused())
            {
                this.pane.setFilter(this.search.getText());
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
        Gui.drawRect(0, 0, width, 30, 0x88000000);
        this.drawString(fontRendererObj, I18n.format("metamorph.gui.creative_title"), 10, 11, 0xffffff);

        /* Draw the builder */
        if (this.builderMode)
        {
            this.builder.draw(mouseX, mouseY, partialTicks);

            AbstractMorph morph = this.builder.currentBuilder.getMorph();

            if (morph != null)
            {
                this.drawCenteredString(fontRendererObj, morph.name, 70, 53, 0x888888);

                morph.renderOnScreen(Minecraft.getMinecraft().thePlayer, 70, height - (int) ((float) height / 2.6), 43, 1.0F);
            }
        }
        else
        {
            /* Draw creative morphs */
            this.fontRendererObj.drawStringWithShadow(I18n.format("metamorph.gui.search"), 146, 41, 0xffffff);
            this.search.drawTextBox();
            this.pane.drawScreen(mouseX, mouseY, partialTicks);

            /* Draw currently selected morph */
            MorphCell morph = this.pane.getSelected();
            String selected = morph != null ? morph.current().name : I18n.format("metamorph.gui.no_morph");

            this.drawCenteredString(fontRendererObj, selected, 70, 41, 0xffffffff);

            if (morph != null)
            {
                this.drawCenteredString(fontRendererObj, morph.current().morph.name, 70, 53, 0x888888);

                morph.current().render(Minecraft.getMinecraft().thePlayer, 70, height - (int) ((float) height / 2.6), 43);
            }
        }

        /* Render buttons */
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}