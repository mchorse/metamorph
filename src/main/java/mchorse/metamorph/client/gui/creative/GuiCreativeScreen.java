package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

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
 */
public class GuiCreativeScreen extends GuiBase
{
    /* GUI stuff */
    private GuiSelectorEditor selectors;
    private GuiIconElement icon;
    private GuiIconElement copy;
    private GuiButtonElement morph;
    private GuiButtonElement acquire;
    private GuiButtonElement close;
    private GuiCreativeMorphs pane;
    private boolean selected;

    public GuiCreativeScreen()
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.selectors = new GuiSelectorEditor(mc);
        this.selectors.setVisible(false);

        this.icon = new GuiIconElement(mc, MMIcons.PROPERTIES, this::toggleEntitySelector);
        this.icon.tooltip("Entity selectors", Direction.BOTTOM);
        this.copy = new GuiIconElement(mc, Icons.COPY, this::copyMorphCommand);
        this.copy.tooltip("Copy /morph command for selected morph", Direction.BOTTOM);
        this.morph = new GuiButtonElement(mc, I18n.format("metamorph.gui.morph"), (b) ->
        {
            this.pane.finish();

            AbstractMorph morph = this.pane.getSelected();

            if (morph != null)
            {
                Dispatcher.sendToServer(new PacketMorph(morph));
                this.closeScreen();
            }
        });
        this.acquire = new GuiButtonElement(mc, I18n.format("metamorph.gui.acquire"), (b) ->
        {
            this.pane.finish();

            AbstractMorph morph = this.pane.getSelected();

            if (morph != null)
            {
                Dispatcher.sendToServer(new PacketAcquireMorph(morph));
            }
        });
        this.close = new GuiButtonElement(mc, I18n.format("metamorph.gui.close"), (b) -> this.closeScreen());
        this.pane = new GuiCreativeMorphs(mc, this::setMorph);
        this.pane.setSelected(Morphing.get(mc.player).getCurrentMorph());

        this.selectors.flex().parent(this.viewport).wh(0.5F, 1F);
        this.morph.flex().parent(this.viewport).set(0, 10, 60, 20).x(1, -200);
        this.acquire.flex().relative(this.morph.resizer()).set(65, 0, 60, 20);
        this.close.flex().relative(this.acquire.resizer()).set(65, 0, 60, 20);
        this.icon.flex().relative(this.morph.resizer()).set(-18, 2, 16, 16);
        this.copy.flex().relative(this.icon.resizer()).set(-20, 0, 16, 16);
        this.pane.flex().parent(this.viewport).set(0, 40, 0, 0).w(1, 0).h(1, -40);

        this.root.add(this.pane, this.icon, this.copy, this.morph, this.acquire, this.close, this.selectors);
    }

    private void copyMorphCommand(GuiIconElement button)
    {
        AbstractMorph morph = this.pane.getSelected();

        if (morph != null)
        {
            NBTTagCompound nbt = morph.toNBT();

            nbt.removeTag("Name");

            String command = "/morph @p " + morph.name + " " + nbt.toString();

            GuiScreen.setClipboardString(command);
        }
    }

    private void toggleEntitySelector(GuiIconElement button)
    {
        this.selectors.toggleVisible();

        if (this.selectors.isVisible())
        {
            this.pane.flex().x(0.5F, 0).w(0.5F, 0);
        }
        else
        {
            this.pane.flex().x(0).w(1F, 0);
        }

        this.pane.resize();
    }

    private void setMorph(AbstractMorph morph)
    {
        this.selectors.setMorph(morph);
        this.copy.setVisible(morph != null);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void closeScreen()
    {
        this.pane.finish();

        super.closeScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        /* Draw panel backgrounds */
        this.drawDefaultBackground();
        Gui.drawRect(this.pane.area.x, 0, this.width, 40, 0xaa000000);
        Gui.drawRect(this.pane.area.x, 39, this.width, 40, 0x44000000);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}