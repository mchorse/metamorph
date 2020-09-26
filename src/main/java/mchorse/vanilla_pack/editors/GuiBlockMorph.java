package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.vanilla_pack.editors.panels.GuiItemStackPanel;
import mchorse.vanilla_pack.morphs.BlockMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		this.registerPanel(this.block, IKey.lang("metamorph.gui.panels.block"), Icons.BLOCK);
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

		this.addPreset(morph, presets, "Stone", "{Block:\"minecraft:stone\"}");
		this.addPreset(morph, presets, "Cobblestone", "{Block:\"minecraft:cobblestone\"}");
		this.addPreset(morph, presets, "Grass", "{Block:\"minecraft:grass\"}");
		this.addPreset(morph, presets, "Dirt", "{Block:\"minecraft:dirt\"}");
		this.addPreset(morph, presets, "Log", "{Block:\"minecraft:log\"}");
		this.addPreset(morph, presets, "Diamond block", "{Block:\"minecraft:diamond_block\"}");
		this.addPreset(morph, presets, "Sponge", "{Block:\"minecraft:sponge\"}");
		this.addPreset(morph, presets, "Deadbush", "{Block:\"minecraft:deadbush\"}");

		return presets;
	}
}