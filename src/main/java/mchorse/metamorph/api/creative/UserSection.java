package mchorse.metamorph.api.creative;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

/**
 * User morph section
 *
 * Here we store acquired morphs, recently edited morphs and custom
 * categories created by the player
 */
public class UserSection extends MorphSection
{
	public MorphCategory acquired;
	public MorphCategory recent;

	public UserSection(String title)
	{
		super(title);

		this.acquired = new MorphCategory(this, "acquired");
		this.recent = new RecentCategory(this, "recent");
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
		this.acquired.morphs = morphing == null ? Collections.emptyList() : morphing.getAcquiredMorphs();
	}

	@Override
	public void reset()
	{
		super.reset();

		this.categories.clear();
		this.acquired.clear();
		this.recent.clear();
	}
}
