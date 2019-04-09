package mchorse.vanilla_pack.editors;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayerMorph extends GuiAbstractMorph<PlayerMorph>
{
    public GuiUsernamePanel username;

    public GuiPlayerMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.username = new GuiUsernamePanel(mc, this);
        this.registerPanel(this.username, GuiAbstractMorph.PANEL_ICONS, I18n.format("metamorph.gui.panels.username"), 16, 0, 16, 16);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof PlayerMorph;
    }
}