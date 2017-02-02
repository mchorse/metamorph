package mchorse.metamorph.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
    private GuiTextField search;
    private GuiCreativeMorphs pane;

    /* Horizontal margin */
    private static final int MARGIN = 20;

    /**
     * Default constructor
     * 
     * This method is responsible for constructing the morphs for rendering and 
     * also selecting the morph that player uses right now.
     */
    public GuiCreativeMenu()
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        IMorphing morphing = Morphing.get(player);

        this.pane = new GuiCreativeMorphs(6, morphing.getCurrentMorph());
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
        int x = width - MARGIN;
        int y = 5;

        morph = new GuiButton(0, x - 190, y, 60, 20, I18n.format("metamorph.gui.morph"));
        acquire = new GuiButton(1, x - 125, y, 60, 20, I18n.format("metamorph.gui.acquire"));
        close = new GuiButton(2, x - 60, y, 60, 20, I18n.format("metamorph.gui.close"));
        search = new GuiTextField(-1, fontRendererObj, 195 + 1, 35 + 1, this.width - 205 - 2, 20 - 2);

        this.buttonList.add(morph);
        this.buttonList.add(acquire);
        this.buttonList.add(close);

        this.pane.setHidden(false);
        this.pane.updateRect(145, 55, this.width - 155, this.height - 60);
        this.pane.scrollBy(0);
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
        MorphCell morph = this.pane.getSelected();

        if (button.id != 1)
        {
            if (button.id == 0)
            {
                Dispatcher.sendToServer(new PacketMorph(morph == null ? null : morph.morph));
            }

            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        else if (morph != null)
        {
            Dispatcher.sendToServer(new PacketAcquireMorph(morph.morph));
        }
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

        this.search.mouseClicked(mouseX, mouseY, mouseButton);
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
        /* Label variables */
        MorphCell morph = this.pane.getSelected();
        String selected = morph != null ? morph.name : I18n.format("metamorph.gui.no_morph");

        /* Draw panel backgrounds */
        this.drawDefaultBackground();
        Gui.drawRect(0, 0, width, 30, 0x88000000);
        this.fontRendererObj.drawStringWithShadow(I18n.format("metamorph.gui.search"), 146, 41, 0xffffffff);

        /* Draw labels */
        this.drawString(fontRendererObj, I18n.format("metamorph.gui.creative_title"), 20, 11, 0xffffff);
        this.drawCenteredString(fontRendererObj, selected, 70, height - 20, 0xffffff);

        this.pane.drawScreen(mouseX, mouseY, partialTicks);
        this.search.drawTextBox();

        if (morph != null)
        {
            this.renderMorph(morph.morph, Minecraft.getMinecraft().thePlayer, 70, height - (int) ((float) height / 2.6), 43);
        }

        /* Disable scissors */
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        /* Render buttons */
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Render a morph 
     */
    private void renderMorph(AbstractMorph morph, EntityPlayer player, int x, int y, float scale)
    {
        /* Render the model */
        morph.renderOnScreen(player, x, y, scale, 1.0F);
    }
}