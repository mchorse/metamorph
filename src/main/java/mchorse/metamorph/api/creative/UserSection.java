package mchorse.metamorph.api.creative;

import mchorse.metamorph.api.creative.MorphCategory;
import mchorse.metamorph.api.creative.MorphSection;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

public class UserSection extends MorphSection
{
	private MorphCategory acquired;

	public UserSection(String title)
	{
		super(title);

		this.acquired = new MorphCategory(this, "acquired");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(World world)
	{
		super.update(world);

		IMorphing morphing = Morphing.get(Minecraft.getMinecraft().player);

		this.categories.clear();
		this.categories.add(this.acquired);
		this.acquired.morphs = morphing == null ? Collections.emptyList() : morphing.getAcquiredMorphs();
	}
}
