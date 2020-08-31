package mchorse.metamorph.client.gui.survival;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.creative.PacketMorph;
import mchorse.metamorph.network.common.survival.PacketFavorite;
import mchorse.metamorph.network.common.survival.PacketKeybind;
import mchorse.metamorph.network.common.survival.PacketRemoveMorph;
import mchorse.metamorph.network.common.survival.PacketSelectMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

/**
 * Survival morph menu GUI
 * 
 * This is menu which allows users to manage their acquired morphs, favorite them,
 * setup keybinds, and when they're in creative mode, they can also manage
 * recent and morphs from custom categories.
 */
public class GuiSurvivalScreen extends GuiBase
{
    public GuiSurvivalMorphs morphs;
    public GuiElement sidebar;
    public GuiToggleElement onlyFavorite;

    public GuiButtonElement morph;
    public GuiButtonElement remove;
    public GuiKeybindElement keybind;
    public GuiToggleElement favorite;

    private boolean creative;
    private boolean allowed;

    public GuiSurvivalScreen()
    {
        super();

        Minecraft mc = this.context.mc;

        this.morph = new GuiButtonElement(mc, IKey.lang("metamorph.gui.morph"), this::morph);
        this.remove = new GuiButtonElement(mc, IKey.lang("metamorph.gui.remove"), this::remove);
        this.keybind = new GuiKeybindElement(mc, this::setKeybind);
        this.keybind.tooltip(IKey.lang("metamorph.gui.survival.keybind_tooltip"));
        this.favorite = new GuiToggleElement(mc, IKey.lang("metamorph.gui.survival.favorite"), this::favorite);

        this.sidebar = new GuiScrollElement(mc);
        this.sidebar.flex().relative(this.root).y(20).w(140).hTo(this.root.resizer(), 1F).column(5).stretch().height(20).padding(10);
        this.sidebar.add(Elements.row(mc, 5, 0, 20, this.morph, this.remove), this.keybind, this.favorite);

        this.onlyFavorite = new GuiToggleElement(mc, IKey.lang("metamorph.gui.survival.only_favorites"), (button) -> this.morphs.setFavorite(button.isToggled()));
        this.onlyFavorite.flex().relative(this.root).x(1F).w(120).h(20).anchor(1F, 0F);

        this.morphs = new GuiSurvivalMorphs(mc);
        this.morphs.flex().relative(this.root).x(140).y(20).wTo(this.root.resizer(), 1F).hTo(this.root.resizer(), 1F).column(0).vertical().stretch().scroll();

        this.root.flex().xy(0.5F, 0.5F).wh(1F, 1F).anchor(0.5F, 0.5F).maxW(500).maxH(300);
        this.root.add(this.morphs, this.sidebar, this.onlyFavorite);

        /* Setup keybinds */
        IKey category = IKey.lang("metamorph.gui.survival.keys.category");

        this.root.keys().register(this.morph.label, Keyboard.KEY_RETURN, () -> this.morph.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(this.remove.label, Keyboard.KEY_BACK, () -> this.remove.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(this.favorite.label, Keyboard.KEY_F, () -> this.favorite.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(IKey.lang("metamorph.gui.survival.keys.toggle_favorites"), Keyboard.KEY_O, () -> this.onlyFavorite.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(IKey.lang("metamorph.gui.survival.keys.focus_keybind"), Keyboard.KEY_K, () -> this.keybind.clickItself(GuiBase.getCurrent())).category(category);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return Metamorph.pauseGUIInSP.get();
    }

    /**
     * Open the survival morph menu and update the morphs element
     */
    public GuiSurvivalScreen open()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        IMorphing cap = Morphing.get(player);
        boolean creative = player.isCreative();
        boolean allowed = Metamorph.allowMorphingIntoCategoryMorphs.get();

        if (this.creative != creative || this.allowed != allowed || creative || this.morphs.sections.isEmpty())
        {
            this.creative = creative;
            this.allowed = allowed;
            this.morphs.setupSections(creative, (section) -> this.fill(section.morph));
        }

        this.setSelected(cap.getCurrentMorph());

        return this;
    }

    /**
     * Set given morph selected
     */
    public void setSelected(AbstractMorph morph)
    {
        this.morphs.setSelected(morph);
        this.fill(this.morphs.getSelected());
    }

    /**
     * Fill the fields with the data from current morph
     */
    public void fill(AbstractMorph morph)
    {
        this.morph.setEnabled(morph != null);
        this.remove.setEnabled(morph != null);
        this.keybind.setEnabled(morph != null);
        this.keybind.setKeybind(Keyboard.KEY_NONE);
        this.favorite.setEnabled(morph != null);

        if (morph != null)
        {
            this.favorite.toggled(morph.favorite);
            this.keybind.setKeybind(morph.keybind);
        }
    }

    /**
     * Morph player into currently selected morph
     */
    private void morph(GuiButtonElement button)
    {
        AbstractMorph morph = this.morphs.getSelected();

        if (morph != null)
        {
            if (this.morphs.isAcquiredSelected())
            {
                Dispatcher.sendToServer(new PacketSelectMorph(this.indexOf(morph)));
            }
            else
            {
                Dispatcher.sendToServer(new PacketMorph(morph));
            }

            this.closeScreen();
        }
    }

    /**
     * Remove currently selected morph
     */
    private void remove(GuiButtonElement button)
    {
        AbstractMorph morph = this.morphs.getSelected();

        if (morph != null)
        {
            if (this.morphs.isAcquiredSelected())
            {
                Dispatcher.sendToServer(new PacketRemoveMorph(this.indexOf(morph)));
            }
            else
            {
                this.morphs.selected.category.remove(morph);
            }

            this.setSelected(null);
        }
    }

    /**
     * Set keybind of the current morph
     */
    private void setKeybind(int keybind)
    {
        AbstractMorph morph = this.morphs.getSelected();

        if (morph != null)
        {
            if (keybind == ClientProxy.keys.keyDemorph.getKeyCode())
            {
                this.keybind.setKeybind(morph.keybind);

                return;
            }

            keybind = keybind == Keyboard.KEY_ESCAPE ? -1 : keybind;
            morph.keybind = keybind;

            if (keybind == -1)
            {
                this.keybind.setKeybind(Keyboard.KEY_NONE);
            }

            if (this.morphs.isAcquiredSelected())
            {
                Dispatcher.sendToServer(new PacketKeybind(this.indexOf(morph), keybind));
            }
            else
            {
                this.morphs.selected.category.edit(morph);
            }
        }
    }

    /**
     * Favorite or unfavorite current morph
     */
    private void favorite(GuiToggleElement button)
    {
        AbstractMorph morph = this.morphs.getSelected();

        if (morph != null)
        {
            if (this.morphs.isAcquiredSelected())
            {
                Dispatcher.sendToServer(new PacketFavorite(this.indexOf(morph)));
            }
            else
            {
                morph.favorite = button.isToggled();
                this.morphs.selected.category.edit(morph);
            }
        }
    }

    private int indexOf(AbstractMorph morph)
    {
        return this.morphs.acquired.getMorphs().indexOf(morph);
    }

    @Override
    public void keyPressed(char typedChar, int keyCode)
    {
        if (keyCode == ClientProxy.keys.keyDemorph.getKeyCode())
        {
            Dispatcher.sendToServer(new PacketSelectMorph(-1));
        }
        else if (MorphManager.INSTANCE.list.keyTyped(this.mc.player, keyCode))
        {
            this.closeScreen();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GuiDraw.drawCustomBackground(this.root.area.x, this.root.area.y, this.root.area.w, this.root.area.h);
        this.sidebar.area.draw(0x88000000);
        Gui.drawRect(this.root.area.x, this.root.area.y, this.root.area.ex(), this.root.area.y + 20, 0xcc000000);
        this.context.font.drawStringWithShadow(I18n.format("metamorph.gui.survival.title"), this.root.area.x + 6, this.root.area.y + 10 - this.context.font.FONT_HEIGHT / 2, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}