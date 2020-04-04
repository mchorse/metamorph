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

import java.util.function.Consumer;

/**
 * Creative morph menu, but with a close button 
 */
public class GuiCreativeMorphsMenu extends GuiCreativeMorphs
{
    private GuiButtonElement close;
    private GuiButtonElement acquire;

    public GuiCreativeMorphsMenu(Minecraft mc, Consumer<AbstractMorph> callback)
    {
        super(mc, callback);

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
            this.finish();
            this.removeFromParent();
        });

        this.acquire.flex().wh(60, 20);
        this.close.flex().wh(20, 20);

        this.bar.prepend(this.acquire);
        this.bar.add(this.close);

        this.markContainer();
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