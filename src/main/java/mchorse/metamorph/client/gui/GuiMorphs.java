package mchorse.metamorph.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import mchorse.metamorph.api.morph.Morph;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

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
public class GuiMorphs extends GuiScreen
{
    /* GUI stuff */
    private GuiButton morph;
    private GuiButton close;

    /* Data stuff */
    private List<MorphCell> morphs = new ArrayList<GuiMorphs.MorphCell>();
    private int selected = -1;
    private float scroll = 0;
    private int perRow = 7;

    /* Initialization code */

    public GuiMorphs()
    {
        Map<String, Morph> morphs = MorphManager.INSTANCE.morphs;
        int index = 0;

        for (String key : new TreeSet<String>(morphs.keySet()))
        {
            this.morphs.add(new MorphCell(key, morphs.get(key), index));

            index++;
        }
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
        int w = width - 40;
        int y = height - 25;
        int x = (width - w) / 2;

        morph = new GuiButton(0, x, y, 100, 20, I18n.format("metamorph.gui.morph"));
        close = new GuiButton(1, x + w - 100, y, 100, 20, I18n.format("metamorph.gui.close"));

        this.buttonList.add(morph);
        this.buttonList.add(close);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            /* Send message to server to morph into selected moprh */
        }

        Minecraft.getMinecraft().displayGuiScreen(null);
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
        super.handleMouseInput();

        int i = -Mouse.getEventDWheel();
        float max = ((this.morphs.size() / this.perRow) + 1) * 60;

        if (i != 0 && max > (height - 60))
        {
            float maxScroll = max - (height - 60);

            this.scroll += Math.copySign(3, i);
            this.scroll = MathHelper.clamp_float(this.scroll, 0.0F, maxScroll);
        }
    }

    /**
     * Mouse clicked GUI event
     * 
     * This method is responsible for selecting a morph based on the mouse 
     * coordinates. If the mouse coordinates aren't pointing at any morph, 
     * then the 
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        /* Don't handle clicks outside of the panel */
        if (mouseY < 30 || mouseY > height - 30)
        {
            return;
        }

        /* Compute the selection index */
        int w = (width - 40);
        int m = w / this.perRow;

        int x = ((mouseX - (width - w) / 2) / m);
        int y = ((mouseY - 30 + (int) this.scroll) / 60);

        int index = x + this.perRow * y;

        if (index >= this.morphs.size() || x < 0 || x > this.perRow)
        {
            this.selected = -1;
        }
        else
        {
            this.selected = index;
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
        int count = this.morphs.size();
        boolean cond = count != 0 && this.selected >= 0 && this.selected < count;
        String selected = cond ? this.morphs.get(this.selected).name : I18n.format("metamorph.gui.no_morph");

        /* Draw panel backgrounds */
        this.drawDefaultBackground();
        drawRect(0, height - 30, width, height, 0x88000000);
        drawRect(0, 0, width, 30, 0x88000000);

        /* Draw labels */
        this.drawCenteredString(fontRendererObj, I18n.format("metamorph.gui.select"), width / 2, 10, 0xffffff);
        this.drawCenteredString(fontRendererObj, selected, width / 2, height - 18, 0xffffff);

        /* Don't run with scissor, or you might get clipped */
        GuiMenu.scissor(0, 30, width, height - 60, width, height);
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        int w = (width - 40);
        int m = w / this.perRow;

        /* Render morphs */
        for (MorphCell cell : this.morphs)
        {
            int i = cell.index;

            int x = i % this.perRow * m + (width - w) / 2;
            int y = i / this.perRow * 60 + 20 - (int) this.scroll;
            float scale = 21.5F;

            /* Render the model */
            cell.model.pose = cell.model.model.poses.get("standing");
            cell.model.swingProgress = 0;

            Minecraft.getMinecraft().renderEngine.bindTexture(cell.model.model.defaultTexture);
            GuiMenu.drawModel(cell.model, player, x + m / 2, y + 60, scale);
        }

        /* Disable scissors */
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        /* Render buttons */
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Morph cell class
     * 
     * An instance of this class represents a morph which can be selected and 
     * morphed into upon pressing "Morph" button.
     */
    public static class MorphCell
    {
        public String name;
        public Morph morph;
        public ModelCustom model;
        public int index;

        public MorphCell(String name, Morph morph, int index)
        {
            this.name = name;
            this.morph = morph;
            this.index = index;

            this.model = ModelCustom.MODELS.get(name);
        }
    }
}