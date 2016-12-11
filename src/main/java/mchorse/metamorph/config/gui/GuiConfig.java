package mchorse.metamorph.config.gui;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.Metamorph;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Config GUI
 *
 * This config GUI is responsible for managing Blockbuster's config. Most of
 * the code that implements config features is located in the parent of the
 * class.
 */
@SideOnly(Side.CLIENT)
public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig
{
    public GuiConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), Metamorph.MODID, false, false, "Metamorph");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> elements = new ArrayList<IConfigElement>();

        for (String name : Metamorph.proxy.forge.getCategoryNames())
        {
            ConfigCategory category = Metamorph.proxy.forge.getCategory(name);

            category.setLanguageKey("metamorph.config." + name + ".title");
            elements.add(new ConfigElement(category));
        }

        return elements;
    }
}