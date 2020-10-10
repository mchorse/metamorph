package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.metamorph.Metamorph;
import net.minecraft.client.Minecraft;

public class GuiSelectorsScreen extends GuiBase
{
	public GuiSelectorEditor editor;
	public GuiCreativeMorphsMenu menu;

	public GuiSelectorsScreen(Minecraft mc)
	{
		this.editor = new GuiSelectorEditor(mc, true);
		this.menu = new GuiCreativeMorphsMenu(mc, true, this.editor::setMorph);
		this.menu.setVisible(true);

		this.editor.flex().relative(this.viewport).wTo(this.menu.flex()).h(1F);
		this.menu.flex().relative(this.viewport).x(140).h(1F).wTo(this.root.flex(), 1F);

		this.root.add(this.menu, this.editor);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return Metamorph.pauseGUIInSP.get();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GuiDraw.drawCustomBackground(0, 0, this.width, this.height);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}