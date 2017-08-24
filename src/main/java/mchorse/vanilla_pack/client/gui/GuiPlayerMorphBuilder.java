package mchorse.vanilla_pack.client.gui;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.client.gui.builder.GuiAbstractMorphBuilder;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Player morph builder
 * 
 * Allows users to build a morph based on player's username. Has also a 
 * little timer so it didn't lagged so much.
 */
public class GuiPlayerMorphBuilder extends GuiAbstractMorphBuilder
{
    public GuiTextField username;
    public int counter;

    public GuiPlayerMorphBuilder()
    {
        super();
        this.username = new GuiTextField(0, this.font, 0, 0, 0, 0);
    }

    private void updateMorph()
    {
        if (this.username.getText().isEmpty())
        {
            return;
        }

        try
        {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("Name", "Player");
            tag.setString("PlayerName", this.username.getText());

            this.cached = MorphManager.INSTANCE.morphFromNBT(tag);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(int x, int y, int w, int h)
    {
        super.update(x, y, w, h);

        this.username.xPosition = x + 60 + 1;
        this.username.yPosition = y + 6;

        this.username.width = w - 62;
        this.username.height = 20 - 2;

        this.username.setMaxStringLength(200);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {}

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.username.textboxKeyTyped(typedChar, keyCode);

        if (this.username.isFocused())
        {
            this.counter = 15;
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (this.counter >= 0)
        {
            this.counter--;

            if (this.counter == 0)
            {
                this.updateMorph();
            }

            String title = "Updating...";
            int width = this.font.getStringWidth(title);

            this.font.drawStringWithShadow(title, this.x + this.w - width, this.username.yPosition - 20, 0xffffff);
        }

        this.username.drawTextBox();
        this.font.drawStringWithShadow("Username", this.x, this.y + 11, 0xffffff);
    }
}