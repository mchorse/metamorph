package mchorse.metamorph.client.gui.builder;

import java.util.HashMap;
import java.util.Map;

import mchorse.metamorph.client.gui.GuiCreativeMenu;
import mchorse.metamorph.client.gui.utils.GuiDropDownField;
import mchorse.metamorph.client.gui.utils.GuiDropDownField.DropDownItem;
import mchorse.metamorph.client.gui.utils.GuiDropDownField.IDropDownListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * Morph builder 
 */
public class GuiMorphBuilder implements IDropDownListener
{
    /**
     * Registry of morph builder panels
     */
    public static final Map<String, IGuiMorphBuilder> BUILDERS = new HashMap<String, IGuiMorphBuilder>();

    /**
     * Parent view references 
     */
    public GuiCreativeMenu parent;

    /**
     * Currently used builder
     */
    public IGuiMorphBuilder currentBuilder;

    /**
     * Drop down field which would allow to pick different morph builders
     */
    public GuiDropDownField dropDown;

    public int x;
    public int y;
    public int w;
    public int h;

    private String stringBuilder = I18n.format("metamorph.gui.builder");

    public GuiMorphBuilder(GuiCreativeMenu parent)
    {
        this.parent = parent;
        this.dropDown = new GuiDropDownField(Minecraft.getMinecraft().fontRendererObj, this);

        for (String key : BUILDERS.keySet())
        {
            String title = I18n.format("metamorph.builder." + key);

            if (title.equals("morph.builder." + key))
            {
                title = key;
            }

            this.dropDown.values.add(new DropDownItem(title, key));
        }

        this.dropDown.setSelected("nbt");
        this.currentBuilder = BUILDERS.get("nbt");
    }

    @Override
    public void clickedDropDown(GuiDropDownField dropDown, String value)
    {
        this.currentBuilder = BUILDERS.get(value);
        this.currentBuilder.update(this.x, this.y, this.w, this.h);
    }

    public void update(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.dropDown.x = x + 40;
        this.dropDown.y = y;
        this.dropDown.w = 135;
        this.dropDown.h = 20;

        this.currentBuilder.update(x, y, w, h);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (!this.dropDown.isInside(mouseX, mouseY))
        {
            this.currentBuilder.mouseClicked(mouseX, mouseY, mouseButton);
        }

        this.dropDown.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.currentBuilder.mouseReleased(mouseX, mouseY, state);
        this.dropDown.mouseReleased(mouseX, mouseY, state);
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        this.currentBuilder.keyTyped(typedChar, keyCode);
    }

    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.currentBuilder.draw(mouseX, mouseY, partialTicks);
        this.dropDown.draw(mouseX, mouseY, this.parent.width, this.parent.height, partialTicks);

        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.stringBuilder, this.x, this.y + 6, 0xffffff);
    }
}