package mchorse.metamorph.client.gui;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
 */
public class GuiCreativeMenu extends GuiBase
{
    /* GUI stuff */
    private GuiButtonElement morph;
    private GuiButtonElement acquire;
    private GuiButtonElement close;
    private GuiCreativeMorphs pane;

    public GuiCreativeMenu()
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        IMorphing morphing = Morphing.get(player);

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
        this.pane = new GuiCreativeMorphs(mc);
        this.pane.setSelected(morphing.getCurrentMorph());

        this.morph.flex().parent(this.viewport).set(0, 10, 60, 20).x(1, -200);
        this.acquire.flex().relative(this.morph.resizer()).set(65, 0, 60, 20);
        this.close.flex().relative(this.acquire.resizer()).set(65, 0, 60, 20);
        this.pane.flex().parent(this.viewport).set(0, 40, 0, 0).w(1, 0).h(1, -40);

        this.root.add(this.pane, this.morph, this.acquire, this.close);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        /* Draw panel backgrounds */
        this.drawDefaultBackground();
        Gui.drawRect(0, 0, this.width, 40, 0xaa000000);
        Gui.drawRect(0, 39, this.width, 40, 0x44000000);

        /* Draw the name of the morph */
        AbstractMorph morph = this.pane.getSelected();
        String selected = morph != null ? morph.getDisplayName() : I18n.format("metamorph.gui.no_morph");

        this.fontRenderer.drawStringWithShadow(selected, 10, 12, 0xffffffff);

        if (morph != null)
        {
            this.fontRenderer.drawStringWithShadow(morph.name, 10, 22, 0x888888);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}