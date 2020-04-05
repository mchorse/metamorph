package mchorse.metamorph.client.gui.survival;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.resizers.layout.ColumnResizer;
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
import mchorse.metamorph.network.common.survival.PacketFavorite;
import mchorse.metamorph.network.common.survival.PacketKeybind;
import mchorse.metamorph.network.common.survival.PacketRemoveMorph;
import mchorse.metamorph.network.common.survival.PacketSelectMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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

        this.morph = new GuiButtonElement(mc, "Morph", this::morph);
        this.remove = new GuiButtonElement(mc, "Remove", this::remove);
        this.keybind = new GuiKeybindElement(mc, this::setKeybind);
        this.keybind.tooltip("With this field, you can bind a key which will morph you into this particular morph (when you press it either in this menu or in the world)");
        this.favorite = new GuiToggleElement(mc, "Favorite", this::favorite);
        this.favorite.flex().h(12);

        this.sidebar = new GuiScrollElement(mc);
        this.sidebar.flex().relative(this.root.resizer()).y(20).w(140).hTo(this.root.resizer(), 1F);
        ColumnResizer.apply(this.sidebar, 5).stretch().height(20).padding(10);
        this.sidebar.add(Elements.row(mc, 5, 0, 20, this.morph, this.remove), this.keybind, this.favorite);

        this.onlyFavorite = new GuiToggleElement(mc, "Only favorites", this::toggleOnlyFavorite);
        this.onlyFavorite.flex().relative(this.root.resizer()).x(1F).wh(100, 20).anchor(1F, 0F);

        this.morphs = new GuiScrollElement(mc);
        this.morphs.flex().relative(this.root.resizer()).x(140).y(20).wTo(this.root.resizer(), 1F).hTo(this.root.resizer(), 1F);
        ColumnResizer.apply(this.morphs, 0).vertical().stretch().scroll();
        this.setupMorphs();

        this.root.flex().xy(0.5F, 0.5F).wh(1F, 1F).anchor(0.5F, 0.5F).maxW(500).maxH(300);
        this.root.add(this.morphs, this.sidebar, this.onlyFavorite);

        this.root.keys()
            .register("Morph", Keyboard.KEY_M, () ->
            {
                this.morph.clickItself(GuiBase.getCurrent());
                return true;
            })
            .register("Remove", Keyboard.KEY_R, () ->
            {
                this.remove.clickItself(GuiBase.getCurrent());
                return true;
            })
            .register("Favorite", Keyboard.KEY_F, () ->
            {
                this.favorite.clickItself(GuiBase.getCurrent());
                return true;
            })
            .register("Toggle favorite morphs", Keyboard.KEY_O, () ->
            {
                this.onlyFavorite.clickItself(GuiBase.getCurrent());
                return true;
            });
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
            Dispatcher.sendToServer(new PacketSelectMorph(this.indexOf(morph)));

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
            section = new MorphSection("User morphs");
            category = new AcquiredCategory(section, "acquired");

            category.setMorph(cap == null ? Collections.emptyList() : cap.getAcquiredMorphs());
            section.add(category);
        }

        GuiMorphSection element = section.getGUI(mc, null, this::setMorph);

        element.flex();

        this.morphs.clear();
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
        this.root.area.draw(0xaa000000);
        this.sidebar.area.draw(0x88000000);
        Gui.drawRect(this.root.area.x, this.root.area.y, this.root.area.ex(), this.root.area.y + 20, 0xcc000000);
        this.context.font.drawStringWithShadow("Survival morphs", this.root.area.x + 6, this.root.area.y + 10 - this.context.font.FONT_HEIGHT / 2, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}