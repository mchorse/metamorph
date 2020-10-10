package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;

public class GuiNestedEdit extends GuiElement
{
	public GuiButtonElement pick;
	public GuiButtonElement edit;

	public GuiNestedEdit(Minecraft mc, Consumer<Boolean> callback)
	{
		this(mc, true, callback);
	}

	public GuiNestedEdit(Minecraft mc, boolean keybinds, Consumer<Boolean> callback)
	{
		super(mc);

		this.edit = new GuiButtonElement(mc, IKey.lang("metamorph.gui.creative.edit"), (b) -> callback.accept(true));
		this.pick = new GuiButtonElement(mc, IKey.lang("metamorph.gui.creative.pick"), (b) -> callback.accept(false));

		this.edit.flex().relative(this).h(1F);
		this.pick.flex().relative(this).h(1F);

		this.flex().h(20).row(0);
		this.add(this.pick, this.edit);

		if (keybinds)
		{
			this.keys().register(this.pick.label, Keyboard.KEY_P, () -> this.pick.clickItself(GuiBase.getCurrent()));
			this.keys().register(this.edit.label, Keyboard.KEY_E, () -> this.edit.clickItself(GuiBase.getCurrent()));
		}
	}

	public void setMorph(AbstractMorph morph)
	{
		this.edit.setEnabled(morph != null);
	}
}