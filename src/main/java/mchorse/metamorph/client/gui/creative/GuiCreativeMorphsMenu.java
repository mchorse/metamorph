package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.creative.PacketAcquireMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

/**
 * Creative morph menu, but with a close button 
 */
public class GuiCreativeMorphsMenu extends GuiCreativeMorphsList
{
    private GuiButtonElement close;
    private GuiButtonElement acquire;
    private boolean menu;

    public GuiCreativeMorphsMenu(Minecraft mc, Consumer<AbstractMorph> callback)
    {
        this(mc, false, callback);
    }

    public GuiCreativeMorphsMenu(Minecraft mc, boolean menu, Consumer<AbstractMorph> callback)
    {
        super(mc, callback);

        this.menu = menu;
        this.acquire = new GuiButtonElement(mc, IKey.lang("metamorph.gui.acquire"), (b) ->
        {
            AbstractMorph cell = this.getSelected();

            if (cell != null)
            {
                Dispatcher.sendToServer(new PacketAcquireMorph(cell));
            }
        });

        this.close = new GuiButtonElement(mc, IKey.str("X"), (b) -> this.exit());

        this.acquire.flex().w(60);
        this.close.flex().w(20);

        this.bar.flex().row(0).preferred(1);
        this.bar.prepend(this.acquire);

        if (!this.menu)
        {
            this.bar.add(this.close);
        }

        this.markContainer();

        this.keys().register(IKey.lang("metamorph.gui.creative.keys.acquire"), Keyboard.KEY_A, () -> this.acquire.clickItself(GuiBase.getCurrent())).category(this.exitKey.category).active(() -> !this.isEditMode());
    }

    @Override
    public void exit()
    {
        if (!this.menu && !this.isEditMode() && !this.isNested())
        {
            this.finish();
            this.removeFromParent();

            GuiBase.getCurrent().setContextMenu(null);
        }
        else
        {
            super.exit();
        }
    }

    @Override
    protected boolean updateExitKey()
    {
        if (this.menu)
        {
            return this.isEditMode() || this.isNested();
        }

        return true;
    }

    @Override
    public boolean isSelectedMorphIsEditable()
    {
        return this.morphs.selected != null && this.morphs.selected.category == this.morphs.user.recent;
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

        if (!this.menu)
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0xaa000000);
        }

        super.draw(context);
    }
}