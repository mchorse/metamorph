package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.util.MMIcons;
import mchorse.vanilla_pack.editors.panels.GuiItemStackPanel;
import mchorse.vanilla_pack.morphs.BlockMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBlockMorph extends GuiAbstractMorph<BlockMorph>
{
	public GuiItemStackPanel block;

	public GuiBlockMorph(Minecraft mc)
	{
		super(mc);

		this.defaultPanel = this.block = new GuiItemStackPanel(mc, this);
		this.registerPanel(this.block, I18n.format("metamorph.gui.panels.block"), MMIcons.BLOCK);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof BlockMorph;
	}

	@Override
	public List<Label<NBTTagCompound>> getPresets(BlockMorph morph)
	{
		List<Label<NBTTagCompound>> presets = new ArrayList<Label<NBTTagCompound>>();

		this.addPreset(presets, "Stone", "{Block:\"minecraft:stone\"}");
		this.addPreset(presets, "Cobblestone", "{Block:\"minecraft:cobblestone\"}");
		this.addPreset(presets, "Grass", "{Block:\"minecraft:grass\"}");
		this.addPreset(presets, "Dirt", "{Block:\"minecraft:dirt\"}");
		this.addPreset(presets, "Log", "{Block:\"minecraft:log\"}");
		this.addPreset(presets, "Diamond block", "{Block:\"minecraft:diamond_block\"}");
		this.addPreset(presets, "Sponge", "{Block:\"minecraft:sponge\"}");
		this.addPreset(presets, "Deadbush", "{Block:\"minecraft:deadbush\"}");

		return presets;
	}

	@Override
	protected void drawMorph(GuiContext context)
	{
		super.drawMorph(context);

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
}