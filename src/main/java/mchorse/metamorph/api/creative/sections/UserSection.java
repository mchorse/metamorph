package mchorse.metamorph.api.creative.sections;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mchorse.mclib.utils.JsonUtils;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.categories.AcquiredCategory;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.categories.RecentCategory;
import mchorse.metamorph.api.creative.categories.UserCategory;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsList;
import mchorse.metamorph.client.gui.creative.GuiMorphSection;
import mchorse.metamorph.client.gui.creative.GuiUserSection;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * User morph section
 *
 * Here we store acquired morphs, recently edited morphs and custom
 * categories created by the player
 */
public class UserSection extends MorphSection
{
	public AcquiredCategory acquired;
	public RecentCategory recent;

	public boolean loaded = false;
	public List<UserCategory> global = new ArrayList<UserCategory>();

	public UserSection(String title)
	{
		super(title);

		this.acquired = new AcquiredCategory(this, "acquired");
		this.recent = new RecentCategory(this, "recent");
	}

	@Override
	public void add(MorphCategory category)
	{
		super.add(category);

		if (category instanceof UserCategory)
		{
			this.global.add((UserCategory) category);
		}

		this.save();
	}

	@Override
	public void remove(MorphCategory category)
	{
		super.remove(category);

		if (category instanceof UserCategory)
		{
			this.global.remove(category);
		}

		this.save();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(World world)
	{
		super.update(world);

		IMorphing morphing = Morphing.get(Minecraft.getMinecraft().player);

		this.categories.clear();
		this.categories.add(this.acquired);
		this.categories.add(this.recent);
		this.acquired.setMorph(morphing == null ? Collections.emptyList() : morphing.getAcquiredMorphs());

		if (!this.loaded)
		{
			this.load();
			this.loaded = true;
		}

		this.categories.addAll(this.global);
	}

	@Override
	public void reset()
	{
		super.reset();

		if (this.loaded)
		{
			this.save();
			this.loaded = false;
		}

		this.categories.clear();
		this.acquired.clear();
		this.recent.clear();
		this.global.clear();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiMorphSection getGUI(Minecraft mc, GuiCreativeMorphsList parent, Consumer<GuiMorphSection> callback)
	{
		return new GuiUserSection(mc, parent, this, callback);
	}

	public void load()
	{
		File file = Metamorph.proxy.list;

		if (!file.exists())
		{
			return;
		}

		try
		{
			List<UserCategory> categories = new ArrayList<UserCategory>();
			String content = FileUtils.readFileToString(file, Charset.defaultCharset());
			JsonArray object = new JsonParser().parse(content).getAsJsonArray();
			int i = 0;

			for (JsonElement entry : object)
			{
				JsonObject cat = entry.getAsJsonObject();
				UserCategory category = new UserCategory(this, cat.get("title").getAsString());

				if (cat.has("morphs"))
				{
					for (JsonElement string : cat.get("morphs").getAsJsonArray())
					{
						try {
							AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(string.getAsString()));

							if (morph != null)
							{
								category.add(morph);
								i ++;
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}

				categories.add(category);
			}

			System.out.println("Loading " + categories.size() + " categories with " + i + " morphs!");

			this.global = categories;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void save()
	{
		if (!this.loaded)
		{
			return;
		}

		JsonArray array = new JsonArray();
		int i = 0;

		for (UserCategory category : this.global)
		{
			JsonObject cat = new JsonObject();
			JsonArray morphs = new JsonArray();

			cat.addProperty("title", category.getTitle());
			cat.add("morphs", morphs);

			for (AbstractMorph morph : category.getMorphs())
			{
				if (morph != null)
				{
					morphs.add(morph.toNBT().toString());

					i ++;
				}
			}

			array.add(cat);
		}

		System.out.println("Saving " + array.size() + " categories with " + i + " morphs to list.json!");

		try
		{
			FileUtils.writeStringToFile(Metamorph.proxy.list, JsonUtils.jsonToPretty(array), Charset.defaultCharset());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
