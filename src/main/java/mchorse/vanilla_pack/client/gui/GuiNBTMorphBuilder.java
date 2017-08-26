package mchorse.vanilla_pack.client.gui;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.client.gui.builder.GuiAbstractMorphBuilder;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

/**
 * NBT morph builder
 * 
 * This morph builder allows to make almost any morph based on morph 
 * name in registry and NBT data.
 */
public class GuiNBTMorphBuilder extends GuiAbstractMorphBuilder
{
    public GuiTextField name;
    public GuiTextField nbt;

    public GuiNBTMorphBuilder()
    {
        super();
        this.name = new GuiTextField(0, this.font, 0, 0, 0, 0);
        this.nbt = new GuiTextField(0, this.font, 0, 0, 0, 0);
    }

    /**
     * Construct the morph 
     */
    private void updateMorph()
    {
        try
        {
            NBTTagCompound tag = new NBTTagCompound();
            String nbt = this.nbt.getText();

            if (!nbt.isEmpty())
            {
                if (!(nbt.startsWith("{") && nbt.endsWith("}")))
                {
                    nbt = "{" + nbt + "}";
                }

                tag.merge(JsonToNBT.getTagFromJson(nbt));
            }

            tag.setString("Name", this.name.getText());

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

        this.name.xPosition = this.nbt.xPosition = x + 60 + 1;
        this.name.yPosition = y + 31;
        this.nbt.yPosition = y + 61;

        this.name.width = this.nbt.width = w - 62;
        this.name.height = this.nbt.height = 20 - 2;

        this.name.setMaxStringLength(200);
        this.nbt.setMaxStringLength(100000);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.nbt.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {}

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.name.textboxKeyTyped(typedChar, keyCode);
        this.nbt.textboxKeyTyped(typedChar, keyCode);

        if (this.name.isFocused() || this.nbt.isFocused())
        {
            this.updateMorph();
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.name.drawTextBox();
        this.nbt.drawTextBox();

        this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.name"), this.x, this.y + 37, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("metamorph.gui.panels.nbt"), this.x, this.y + 67, 0xffffff);
    }
}