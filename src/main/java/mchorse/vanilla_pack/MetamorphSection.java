package mchorse.vanilla_pack;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.vanilla_pack.morphs.BlockMorph;
import mchorse.vanilla_pack.morphs.ItemMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MetamorphSection extends MorphSection
{
	private MetamorphFactory factory;
	private Map<String, MorphCategory> categoryMap = new HashMap<String, MorphCategory>();

	public MetamorphSection(MetamorphFactory factory, String title)
	{
		super(title);

		this.factory = factory;
	}

	@Override
	public void update(World world)
	{
		if (!this.categories.isEmpty())
		{
			return;
		}

		for (ResourceLocation rl : EntityList.getEntityNameList())
		{
			String name = MorphManager.INSTANCE.remap(rl.toString());

			if (this.factory.hasMorph(name))
			{
				this.addMorph(world, name);
			}
		}

		/* TODO: use that data for quick access list
		this.addMorph(morphs, world, "minecraft:pig", "{Age:-1}");
		this.addMorph(morphs, world, "minecraft:chicken", "{Age:-1}");
		this.addMorph(morphs, world, "minecraft:cow", "{Age:-1}");
		this.addMorph(morphs, world, "minecraft:mooshroom", "{Age:-1}");
		this.addMorph(morphs, world, "minecraft:polar_bear", "{Age:-1}");

		this.addMorph(morphs, world, "minecraft:sheep", "{Sheared:1b}");
		this.addMorph(morphs, world, "minecraft:sheep", "{Age:-1}");
		this.addMorph(morphs, world, "minecraft:sheep", "{Age:-1,Sheared:1b}");

		for (int i = 1; i < 16; i++)
		{
			this.addMorph(morphs, world, "minecraft:sheep", "{Color:" + i + "}");
		}

		this.addMorph(morphs, world, "minecraft:sheep", "Jeb", "{CustomName:\"jeb_\"}");
		this.addMorph(morphs, world, "minecraft:sheep", "Baby Jeb", "{Age:-1,CustomName:\"jeb_\"}");

		this.addMorph(morphs, world, "minecraft:slime", "{Size:1}");
		this.addMorph(morphs, world, "minecraft:slime", "{Size:2}");

		this.addMorph(morphs, world, "minecraft:magma_cube", "{Size:1}");
		this.addMorph(morphs, world, "minecraft:magma_cube", "{Size:2}");

		this.addMorph(morphs, world, "minecraft:ocelot", "{Age:-1}");

		for (int i = 1; i <= 4; i++)
		{
			this.addMorph(morphs, world, "minecraft:parrot", "{Variant:" + i + "}");
		}

		for (int i = 1; i < 4; i++)
		{
			this.addMorph(morphs, world, "minecraft:ocelot", "{CatType:" + i + "}");
			this.addMorph(morphs, world, "minecraft:ocelot", "{CatType:" + i + ",Age:-1}");
		}

		this.addMorph(morphs, world, "minecraft:horse", "{Variant:1}");
		this.addMorph(morphs, world, "minecraft:horse", "{Variant:2}");
		this.addMorph(morphs, world, "minecraft:horse", "{Variant:3}");
		this.addMorph(morphs, world, "minecraft:horse", "{Variant:4}");
		this.addMorph(morphs, world, "minecraft:horse", "{Variant:5}");
		this.addMorph(morphs, world, "minecraft:horse", "{Variant:6}");

		this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:librarian\"}");
		this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:priest\"}");
		this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:smith\"}");
		this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:butcher\"}");
		this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:nitwit\"}");

		this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:librarian\"}");
		this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:priest\"}");
		this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:smith\"}");
		this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:butcher\"}");
		this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:nitwit\"}");

		this.addMorph(morphs, world, "minecraft:bat", "{BatFlags:2}");

		this.addMorph(morphs, world, "minecraft:zombie", "Baby", "{IsBaby:1b}");

		for (int i = 1; i < 4; i++)
		{
			this.addMorph(morphs, world, "minecraft:llama", "{Variant:" + i + "}");
		}

		for (int i = 1; i < 6; i++)
		{
			this.addMorph(morphs, world, "minecraft:rabbit", "{RabbitType:" + i + "}");
		}

		this.addMorph(morphs, world, "minecraft:rabbit", "Toast", "{CustomName:\"Toast\"}");

		this.addBlockMorph(morphs, world, "{Block:\"minecraft:stone\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:cobblestone\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:grass\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:dirt\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:log\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:diamond_block\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:sponge\"}");
		this.addBlockMorph(morphs, world, "{Block:\"minecraft:deadbush\"}");
		 */

		/* Miscellaneous morphs */
		PlayerMorph notch = new PlayerMorph();
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("Name", "player");
		tag.setString("Username", "Notch");
		notch.fromNBT(tag);

		this.get("generic").add(new BlockMorph());
		this.get("generic").add(new ItemMorph());
		this.get("generic").add(notch);

		/* Add categories to the main list */
		Iterator<MorphCategory> it = this.categoryMap.values().iterator();

		while (it.hasNext())
		{
			if (it.next().getMorphs().isEmpty())
			{
				it.remove();
			}
		}

		this.categories.addAll(this.categoryMap.values());
		this.categoryMap.clear();
	}

	/**
	 * Add an entity morph to the morph list
	 */
	private void addMorph(World world, String name)
	{
		try
		{
			EntityMorph morph = this.factory.morphFromName(name);
			EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByIDFromName(new ResourceLocation(name), world);

			if (entity == null)
			{
				System.out.println("Couldn't add morph " + name + ", because it's null!");
				return;
			}

			NBTTagCompound data = entity.serializeNBT();

			morph.name = name;

			/* Setting up a category */
			String category = "generic";

			/* Category for third-party modded mobs */
			if (!name.startsWith("minecraft:"))
			{
				category = name.substring(0, name.indexOf(":"));
			}
			else if (entity instanceof EntityDragon || entity instanceof EntityWither || entity instanceof EntityGiantZombie)
			{
				category = "boss";
			}
			else if (entity instanceof EntityAnimal || name.equals("minecraft:bat") || name.equals("minecraft:squid"))
			{
				category = "animal";
			}
			else if (entity instanceof EntityMob || name.equals("minecraft:ghast") || name.equals("minecraft:magma_cube") || name.equals("minecraft:slime") || name.equals("minecraft:shulker"))
			{
				category = "hostile";
			}

			EntityUtils.stripEntityNBT(data);
			morph.setEntityData(data);

			this.get(category).add(morph);
		}
		catch (Exception e)
		{
			System.out.println("An error occured during insertion of " + name + " morph!");
			e.printStackTrace();
		}
	}

	/**
	 * Get a temporary category
	 */
	private MorphCategory get(String name)
	{
		MorphCategory cat = this.categoryMap.get(name);

		if (cat == null)
		{
			this.categoryMap.put(name, cat = new MorphCategory(this, name));
		}

		return cat;
	}

	@Override
	public void reset()
	{
		this.categories.clear();
	}
}