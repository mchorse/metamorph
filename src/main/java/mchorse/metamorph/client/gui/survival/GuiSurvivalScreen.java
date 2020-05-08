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
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.MorphList;
import mchorse.metamorph.api.creative.categories.AcquiredCategory;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.creative.sections.UserSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.creative.GuiMorphSection;
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

import java.util.Collections;

/**
 * Survival morph menu GUI
 * 
 * This is menu which allows users to manage their acquired morphs.
 */
public class GuiSurvivalScreen extends GuiBase
{
    public GuiScrollElement morphs;
    public GuiElement sidebar;
    public GuiToggleElement onlyFavorite;

    public GuiButtonElement morph;
    public GuiButtonElement remove;
    public GuiKeybindElement keybind;
    public GuiToggleElement favorite;

    private GuiMorphSection selected;
    private AcquiredCategory acquired;
    private boolean creative;

    public GuiSurvivalScreen()
    {
        super();

        Minecraft mc = this.context.mc;

        this.morph = new GuiButtonElement(mc, IKey.lang("metamorph.gui.morph"), this::morph);
        this.remove = new GuiButtonElement(mc, IKey.lang("metamorph.gui.remove"), this::remove);
        this.keybind = new GuiKeybindElement(mc, this::setKeybind);
        this.keybind.tooltip(IKey.lang("metamorph.gui.survival.keybind_tooltip"));
        this.favorite = new GuiToggleElement(mc, IKey.lang("metamorph.gui.survival.favorite"), this::favorite);
        this.favorite.flex().h(12);

        this.sidebar = new GuiScrollElement(mc);
        this.sidebar.flex().relative(this.root).y(20).w(140).hTo(this.root.resizer(), 1F).column(5).stretch().height(20).padding(10);
        this.sidebar.add(Elements.row(mc, 5, 0, 20, this.morph, this.remove), this.keybind, this.favorite);

        this.onlyFavorite = new GuiToggleElement(mc, IKey.lang("metamorph.gui.survival.only_favorites"), this::toggleOnlyFavorite);
        this.onlyFavorite.flex().relative(this.root).x(1F).wh(100, 20).anchor(1F, 0F);

        this.morphs = new GuiScrollElement(mc);
        this.morphs.flex().relative(this.root).x(140).y(20).wTo(this.root.resizer(), 1F).hTo(this.root.resizer(), 1F).column(0).vertical().stretch().scroll();
        this.setupMorphs();

        this.root.flex().xy(0.5F, 0.5F).wh(1F, 1F).anchor(0.5F, 0.5F).maxW(500).maxH(300);
        this.root.add(this.morphs, this.sidebar, this.onlyFavorite);

        IKey category = IKey.lang("metamorph.gui.survival.keys.category");

        this.root.keys().register(this.morph.label, Keyboard.KEY_M, () -> this.morph.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(this.remove.label, Keyboard.KEY_R, () -> this.remove.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(this.favorite.label, Keyboard.KEY_F, () -> this.favorite.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(IKey.lang("metamorph.gui.survival.keys.toggle_favorites"), Keyboard.KEY_O, () -> this.onlyFavorite.clickItself(GuiBase.getCurrent())).category(category);
        this.root.keys().register(IKey.lang("metamorph.gui.survival.keys.focus_keybind"), Keyboard.KEY_K, () -> this.keybind.clickItself(GuiBase.getCurrent())).category(category);
    }

    public GuiSurvivalScreen open()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        IMorphing cap = Morphing.get(player);
        boolean creative = player.isCreative();

        if (this.creative != creative || creative)
        {
            this.creative = creative;
            this.setupMorphs();
        }

        this.fill(null);
        this.setSelected(cap.getCurrentMorph());

        return this;
    }

    private void morph(GuiButtonElement button)
    {
        AbstractMorph morph = this.getSelected();

        if (morph != null)
        {
            if (this.selected.category == this.acquired)
            {
                Dispatcher.sendToServer(new PacketSelectMorph(this.indexOf(morph)));
            }
            else if (this.creative)
            {
                Dispatcher.sendToServer(new PacketMorph(morph));
            }

            this.closeScreen();
        }
    }

    private void remove(GuiButtonElement button)
    {
        AbstractMorph morph = this.getSelected();

        if (morph != null && !(this.selected.category instanceof UserCategory))
        {
            int index = this.indexOf(morph);

            Dispatcher.sendToServer(new PacketRemoveMorph(index));
            this.setSelected(null);
        }
    }

    private void setKeybind(int keybind)
    {
        AbstractMorph morph = this.getSelected();

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

            if (this.selected.category instanceof UserCategory)
            {
                this.selected.category.edit(morph);
            }
            else
            {
                Dispatcher.sendToServer(new PacketKeybind(this.indexOf(morph), keybind));
            }
        }
    }

    private void favorite(GuiToggleElement button)
    {
        AbstractMorph morph = this.getSelected();

        if (morph != null)
        {
            if (this.selected.category instanceof UserCategory)
            {
                this.selected.category.edit(morph);
            }
            else
            {
                Dispatcher.sendToServer(new PacketFavorite(this.indexOf(morph)));
            }
        }
    }

    private int indexOf(AbstractMorph morph)
    {
        return this.acquired.getMorphs().indexOf(morph);
    }

    private void toggleOnlyFavorite(GuiToggleElement button)
    {
        this.selected.favorite = button.isToggled();
    }

    private void setupMorphs()
    {
        Minecraft mc = Minecraft.getMinecraft();
        MorphList list = MorphManager.INSTANCE.list;
        IMorphing cap = Morphing.get(mc.player);

        MorphSection section;
        AcquiredCategory category;

        if (this.creative)
        {
            UserSection user = (UserSection) list.sections.get(0);

            section = user;
            section.update(mc.world);
            category = user.acquired;
        }
        else
        {
            section = new MorphSection("user");
            category = new AcquiredCategory(section, "acquired");

            category.setMorph(cap == null ? Collections.emptyList() : cap.getAcquiredMorphs());
            section.add(category);
        }

        GuiMorphSection element = section.getGUI(mc, null, this::setMorph);

        element.flex();

        this.morphs.removeAll();
        this.morphs.add(element);
        this.selected = element;
        this.acquired = category;
    }

    private void setMorph(GuiMorphSection section)
    {
        this.fill(section.morph);
    }

    public AbstractMorph getSelected()
    {
        return this.selected == null ? null : this.selected.morph;
    }

    public void setSelected(AbstractMorph morph)
    {
        if (this.selected != null)
        {
            this.selected.reset();
        }

        if (morph != null)
        {
            AbstractMorph found = this.acquired.getEqual(morph);

            if (found != null)
            {
                this.selected.category = this.acquired;
                this.selected.morph = found;
            }

            this.fill(found);
        }
    }

    public void fill(AbstractMorph morph)
    {
        this.morph.setEnabled(morph != null);
        this.remove.setEnabled(morph != null);
        this.keybind.setEnabled(morph != null);
        this.keybind.setKeybind(Keyboard.KEY_NONE);
        this.favorite.setEnabled(morph != null);

        if (morph != null)
        {
            if (!(this.selected.category instanceof AcquiredCategory))
            {
                this.remove.setEnabled(false);
            }

            this.favorite.toggled(morph.favorite);
            this.keybind.setKeybind(morph.keybind);
        }
    }

    @Override
    public void keyPressed(char typedChar, int keyCode)
    {
        if (keyCode == ClientProxy.keys.keyDemorph.getKeyCode())
        {
            Dispatcher.sendToServer(new PacketSelectMorph(-1));
        }
        else if (!MorphManager.INSTANCE.list.keyTyped(this.mc.player, keyCode))
        {
            return;
        }

        this.closeScreen();
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