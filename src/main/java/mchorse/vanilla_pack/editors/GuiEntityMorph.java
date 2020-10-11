package mchorse.vanilla_pack.editors;

import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.bodypart.GuiBodyPartEditor;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiEntityMorph extends GuiAbstractMorph<EntityMorph>
{
	public static final List<String> animals = Arrays.asList("Pig", "Chicken", "Cow", "MushroomCow", "PolarBear", "Sheep", "Ozelot");

	public GuiBodyPartEditor bodyPart;

	public GuiEntityMorph(Minecraft mc)
	{
		super(mc);

		this.bodyPart = new GuiBodyPartEditor(mc, this);
		this.registerPanel(this.bodyPart, IKey.lang("metamorph.gui.body_parts.parts"), Icons.LIMB);
	}

	@Override
	public boolean canEdit(AbstractMorph morph)
	{
		return morph instanceof EntityMorph;
	}

	@Override
	public void startEdit(EntityMorph morph)
	{
		if (morph.getEntity() == null)
		{
			morph.setupEntity(this.mc.world);
		}

		morph.parts.reinitBodyParts();
		morph.setupLimbs();
		this.bodyPart.setLimbs(morph.limbs.keySet());

		super.startEdit(morph);
	}

	@Override
	public List<Label<NBTTagCompound>> getPresets(EntityMorph morph)
	{
		List<Label<NBTTagCompound>> presets = new ArrayList<Label<NBTTagCompound>>();
		String name = morph.name;

		if (animals.contains(name))
		{
			this.addPreset(morph, presets, "Baby", "{Age:-1}");
		}

		if (name.equals("Sheep"))
		{
			this.addPreset(morph, presets, "Sheared", "{Sheared:1b}");
			this.addPreset(morph, presets, "Sheared (baby)", "{Age:-1,Sheared:1b}");

			for (int i = 1; i < 16; i++)
			{
				this.addPreset(morph, presets, "Colored sheep #" + i, "{Color:" + i + "}");
			}

			this.addPreset(morph, presets, "Jeb", "{CustomName:\"jeb_\"}");
			this.addPreset(morph, presets, "Baby Jeb", "{Age:-1,CustomName:\"jeb_\"}");
		}

		if (name.equals("Slime") || name.equals("LavaSlime"))
		{
			this.addPreset(morph, presets, "Medium", "{Size:1}");
			this.addPreset(morph, presets, "Big", "{Size:2}");
		}

		if (name.equals("Ozelot"))
		{
			for (int i = 1; i < 4; i++)
			{
				this.addPreset(morph, presets, "Cat #" + i, "{CatType:" + i + "}");
				this.addPreset(morph, presets, "Cat #" + i + " (baby)", "{CatType:" + i + ",Age:-1}");
			}
		}

		if (name.equals("EntityHorse"))
		{
			for (int i = 1; i <= 6; i++)
			{
				this.addPreset(morph, presets, "Horse #" + i, "{Type:0,Variant:" + i + "}");
			}
            this.addPreset(morph, presets, "Donkey", "{Type:1,Variant:0}");
            this.addPreset(morph, presets, "Mule", "{Type:2,Variant:0}");
            this.addPreset(morph, presets, "Zombie", "{Type:3,Variant:0}");
            this.addPreset(morph, presets, "Skeleton", "{Type:4,Variant:0}");
		}

		if (name.equals("Bat"))
		{
			this.addPreset(morph, presets, "Flying", "{BatFlags:2}");
		}

		if (name.equals("Rabbit"))
		{
			for (int i = 1; i < 6; i++)
			{
				this.addPreset(morph, presets, "Rabbit #" + i, "{RabbitType:" + i + "}");
			}

			this.addPreset(morph, presets, "Toast", "{CustomName:\"Toast\"}");
		}

        if (name.equals("Skeleton"))
        {
            this.addPreset(morph, presets, "Wither", "{SkeltonType:1}");
            this.addPreset(morph, presets, "Stray", "{SkeltonType:2}");
        }

		if (name.equals("Zombie"))
		{
			this.addPreset(morph, presets, "Baby", "{IsBaby:1b}");

	        for (int i = 1; i < 7; i++)
	        {
	            this.addPreset(morph, presets, "Zombie #" + i, "{ZombieType:" + i + "}");
	        }
		}

        if (name.equals("Guardian"))
        {
            this.addPreset(morph, presets, "Elder", "{Elder:1b}");
        }

		if (name.equals("Villager"))
		{
			this.addPreset(morph, presets, "Librarian", "{ProfessionName:\"minecraft:librarian\"}");
			this.addPreset(morph, presets, "Priest", "{ProfessionName:\"minecraft:priest\"}");
			this.addPreset(morph, presets, "Smith", "{ProfessionName:\"minecraft:smith\"}");
			this.addPreset(morph, presets, "Butcher", "{ProfessionName:\"minecraft:butcher\"}");
			this.addPreset(morph, presets, "Nitwit", "{ProfessionName:\"minecraft:nitwit\"}");
		}

		return presets;
	}

	@Override
	protected void addPreset(AbstractMorph morph, List<Label<NBTTagCompound>> list, String label, String json)
	{
		try
		{
			NBTTagCompound tag = morph.toNBT();
			NBTTagCompound entity = new NBTTagCompound();

			tag.removeTag("EntityData");
			entity.setTag("EntityData", JsonToNBT.getTagFromJson(json));
			tag.merge(entity);
			list.add(new Label<NBTTagCompound>(IKey.str(label), tag));
		}
		catch (Exception e)
		{}
	}
}