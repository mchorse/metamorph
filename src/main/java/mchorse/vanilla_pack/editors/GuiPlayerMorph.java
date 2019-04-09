package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
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

    public static class GuiUsernamePanel extends GuiMorphPanel<PlayerMorph>
    {
        public GuiTextElement username;
        private int counter = -1;

        public GuiUsernamePanel(Minecraft mc, GuiAbstractMorph editor)
        {
            super(mc, editor);

            this.username = new GuiTextElement(mc, 120, (str) -> this.editUsername(str));
            this.username.resizer().parent(this.area).set(10, 10, 0, 20).w(1, -20).y(1, -30);

            this.children.add(this.username);
        }

        private void editUsername(String str)
        {
            if (str.isEmpty())
            {
                return;
            }

            this.counter = 15;
        }

        @Override
        public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
        {
            if (this.counter >= 0)
            {
                if (this.counter == 0 && !this.username.field.getText().isEmpty())
                {
                    PlayerMorph morph = this.morph;

                    morph.resetEntity();
                    morph.setProfile(this.username.field.getText());
                }

                String updating = I18n.format("metamorph.gui.panels.updating");
                int w = this.font.getStringWidth(updating);

                this.font.drawStringWithShadow(updating, this.username.area.getX(1) - w, this.username.area.y - 12, 0xaaaaaa);
                this.counter--;
            }

            super.draw(tooltip, mouseX, mouseY, partialTicks);

            if (this.username.isVisible())
            {
                this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.username"), this.username.area.x, this.username.area.y - 12, 0xffffff);
            }
        }
    }
}