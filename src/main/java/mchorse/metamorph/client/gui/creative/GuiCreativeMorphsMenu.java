package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

/**
 * Creative morph menu, but with a close button 
 */
public class GuiCreativeMorphsMenu extends GuiCreativeMorphs
{
    private GuiButtonElement close;
    private GuiButtonElement acquire;

    public GuiCreativeMorphsMenu(Minecraft mc)
    {
        super(mc);

        this.acquire = new GuiButtonElement(mc, I18n.format("metamorph.gui.acquire"), (b) ->
        {
            AbstractMorph cell = this.getSelected();

            if (cell != null)
            {
                Dispatcher.sendToServer(new PacketAcquireMorph(cell));
            }
        });

        this.close = new GuiButtonElement(mc, "X", (b) ->
        {
            if (this.isEditMode())
            {
                this.setMorph(this.getSelected());
            }

            this.setVisible(false);
        });

        this.acquire.flex().parent(this.area).set(10, 10, 60, 20);
        this.close.flex().parent(this.area).set(0, 10, 20, 20).x(1, -30);
        this.edit.flex().x(1, -35 - 25 - 55);
        this.add(this.acquire, this.close);

        this.search.flex().set(75, 10, 0, 20).w(1, -130 - 65);

        this.hideTooltip();
        this.setVisible(false);
    }

    @Override
    public AbstractMorph setSelected(AbstractMorph morph)
    {
        morph = super.setSelected(morph);

        /* The unknown morph that can't be found in the morph picker 
         * will get cloned, so we have to retrieve it */
        this.setMorph(this.getSelected());

        return morph;
    }

    @Override
    public void toggleEditMode()
    {
        super.toggleEditMode();

        this.acquire.setVisible(this.editor.delegate == null);
        this.close.setVisible(this.editor.delegate == null);
    }

    /* Don't let click event pass through the background... */

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.area.isInside(context.mouseX, context.mouseY);
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return super.mouseScrolled(context) || this.area.isInside(context.mouseX, context.mouseY);
    }

    @Override
    public void draw(GuiContext context)
    {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0xaa000000);

        super.draw(context);
    }
}