package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayerMorph extends GuiAbstractMorph
{
    public GuiTextElement username;
    public GuiButtonElement<GuiButton> toggle;

    private int counter = -1;

    public GuiPlayerMorph(Minecraft mc)
    {
        super(mc);

        this.username = new GuiTextElement(mc, 120, (str) -> this.editUsername(str));
        this.username.resizer().parent(this.area).set(10, 10, 0, 20).w(1, -20).y(1, -30);

        this.toggle = GuiButtonElement.button(mc, I18n.format("metamorph.gui.panels.nbt"), (b) ->
        {
            this.username.toggleVisible();
            this.data.toggleVisible();
            this.updateNBT();
        });
        this.toggle.resizer().parent(this.area).set(0, 10, 60, 20).x(1, -70);

        this.data.toggleVisible();
        this.children.add(this.username, this.toggle);
    }

    private void editUsername(String str)
    {
        this.counter = 15;
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof PlayerMorph;
    }

    @Override
    public void startEdit(AbstractMorph morph)
    {
        super.startEdit(morph);

        this.username.setText(((PlayerMorph) morph).profile.getName());
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.counter >= 0)
        {
            if (this.counter == 0)
            {
                PlayerMorph morph = (PlayerMorph) this.morph;

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
            this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.username"), this.username.area.x, this.username.area.y - 12, this.error ? 0xffff3355 : 0xffffff);
        }
    }
}