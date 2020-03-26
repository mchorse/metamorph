package mchorse.metamorph.client.gui.elements;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
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

    public GuiCreativeMorphsMenu(Minecraft mc, int perRow, AbstractMorph selected, IMorphing morphing)
    {
        super(mc, perRow, selected, morphing);

        this.acquire = new GuiButtonElement(mc, I18n.format("metamorph.gui.acquire"), (b) ->
        {
            MorphCell cell = this.getSelected();

            if (cell != null)
            {
                Dispatcher.sendToServer(new PacketAcquireMorph(cell.current().morph));
            }
        });

        this.close = new GuiButtonElement(mc, "X", (b) ->
        {
            if (this.isEditMode())
            {
                this.setMorph(this.getSelected().current().morph);
            }

            this.setVisible(false);
        });

        this.acquire.resizer().parent(this.area).set(10, 10, 60, 20);
        this.close.resizer().parent(this.area).set(0, 10, 20, 20).x(1, -30);
        this.edit.resizer().x(1, -35 - 25 - 55);
        this.add(this.acquire, this.close);

        this.search.resizer().set(75, 10, 0, 20).w(1, -130 - 65);

        this.hideTooltip();
        this.setVisible(false);
        this.shiftX = 8;
    }

    @Override
    public void resize()
    {
        super.resize();
        int perRow = (int) Math.ceil(this.area.w / 50.0F);

        this.setPerRow(perRow == 0 ? 1 : perRow);
    }

    @Override
    public void setSelected(AbstractMorph morph)
    {
        super.setSelected(morph);

        /* The unknown morph that can't be found in the morph picker 
         * will get cloned, so we have to retrieve it */
        MorphCell cell = this.getSelected();

        this.setMorph(cell == null ? null : cell.current().morph);
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

        MorphCell cell = this.getSelected();

        if (cell != null && !this.isEditMode())
        {
            int width = Math.max(this.font.getStringWidth(cell.current().name), this.font.getStringWidth(cell.current().morph.name)) + 6;
            int center = this.area.mx();
            int y = this.area.y + 40;

            Gui.drawRect(center - width / 2, y - 4, center + width / 2, y + 24, 0x88000000);

            this.drawCenteredString(this.font, cell.current().name, center, y, 0xffffff);
            this.drawCenteredString(this.font, cell.current().morph.name, center, y + 14, 0x888888);
        }

        super.draw(context);
    }
}