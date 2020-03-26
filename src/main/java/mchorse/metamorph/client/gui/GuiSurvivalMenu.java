package mchorse.metamorph.client.gui;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs;
import mchorse.metamorph.client.gui.elements.GuiSurvivalMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Survival morph menu GUI
 * 
 * This is menu which allows users to manage their acquired morphs.
 */
public class GuiSurvivalMenu extends GuiBase
{
    private GuiButtonElement close;
    private GuiButtonElement favorite;
    private GuiButtonElement remove;
    private GuiButtonElement onlyFavorites;
    private GuiButtonElement morph;

    private GuiSurvivalMorphs morphs;

    /* Initiate GUI */

    public GuiSurvivalMenu(GuiSurvivalMorphs morphs)
    {
        this.morphs = morphs;
        this.morphs.inGUI = true;

        Minecraft mc = Minecraft.getMinecraft();

        this.remove = new GuiButtonElement(mc, I18n.format("metamorph.gui.remove"), (b) ->
        {
            this.morphs.remove();
            this.updateButtons();
        });

        this.favorite = new GuiButtonElement(mc, "", (b) ->
        {
            this.morphs.favorite(this.morphs.morphs.get(this.morphs.index).current());
            this.updateButtons();
        });

        this.close = new GuiButtonElement(mc, I18n.format("metamorph.gui.close"), (b) -> this.exit());

        this.onlyFavorites = new GuiButtonElement(mc, "", (b) ->
        {
            this.morphs.toggleFavorites();
            this.updateFavorites();
        });

        this.morph = new GuiButtonElement(mc, I18n.format("metamorph.gui.morph"), (b) ->
        {
            this.morphs.selectCurrent();
            this.exit();
        });

        this.remove.resizer().parent(this.viewport).set(20, 0, 60, 20).y(1, -30);
        this.morph.resizer().parent(this.viewport).set(0, 0, 60, 20).x(1, -80).y(1, -30);
        this.favorite.resizer().relative(this.morph.resizer()).set(-65, 0, 60, 20);

        this.close.resizer().parent(this.viewport).set(0, 10, 60, 20).x(1, -80);
        this.onlyFavorites.resizer().relative(this.close.resizer()).set(-95, 0, 90, 20);

        this.root.add(this.remove, this.favorite, this.close, this.onlyFavorites, this.morph);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.updateFavorites();
        this.updateButtons();
    }

    private void updateFavorites()
    {
        this.onlyFavorites.label = this.morphs.showFavorites ? I18n.format("metamorph.gui.all_morphs") : I18n.format("metamorph.gui.only_favorites");
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.morphs.clickMorph(mouseX, mouseY, this.width, this.height);
        this.updateButtons();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.exit();
        }

        super.keyTyped(typedChar, keyCode);

        if (ClientProxy.keys.keyPrevVarMorph.getKeyCode() == keyCode)
        {
            this.morphs.down();
            this.updateButtons();
        }
        else if (ClientProxy.keys.keyNextVarMorph.getKeyCode() == keyCode)
        {
            this.morphs.up();
            this.updateButtons();
        }
        else if (ClientProxy.keys.keyPrevMorph.getKeyCode() == keyCode)
        {
            this.morphs.advance(-1);
            this.updateButtons();
        }
        else if (ClientProxy.keys.keyNextMorph.getKeyCode() == keyCode)
        {
            this.morphs.advance(1);
            this.updateButtons();
        }
        else if (ClientProxy.keys.keyDemorph.getKeyCode() == keyCode)
        {
            this.morphs.skip(-1);
            this.updateButtons();
        }
        else if (ClientProxy.keys.keySelectMorph.getKeyCode() == keyCode)
        {
            this.morphs.selectCurrent();
            this.exit();
        }
    }

    /**
     * Exit from this GUI 
     */
    private void exit()
    {
        this.morphs.exitGUI();
        this.mc.displayGuiScreen(null);
    }

    public void updateButtons()
    {
        int index = this.morphs.index;

        this.favorite.setEnabled(index >= 0);
        this.favorite.label = I18n.format("metamorph.gui.favorite");
        this.remove.setEnabled(index >= 0);

        if (this.favorite.isEnabled())
        {
            MorphCell cell = this.morphs.getCurrent();

            if (cell != null)
            {
                this.favorite.label = cell.morph.favorite ? I18n.format("metamorph.gui.unfavorite") : I18n.format("metamorph.gui.favorite");
            }
            else
            {
                this.favorite.setEnabled(false);
            }
        }
    }

    /* Drawing code */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        /* Background and stuff */
        this.drawDefaultBackground();

        Gui.drawRect(0, 0, width, 40, 0x88000000);
        this.drawString(this.fontRenderer, I18n.format("metamorph.gui.survival_title"), 20, 16, 0xffffff);

        this.morphs.render(this.width, this.height);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}