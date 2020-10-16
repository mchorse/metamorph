package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ItemStackMorph extends AbstractMorph
{
	public boolean lighting = true;

	public abstract void setStack(ItemStack stack);

	public abstract ItemStack getStack();

	@Override
	public boolean equals(Object obj)
	{
		boolean result = super.equals(obj);

		if (obj instanceof ItemStackMorph)
		{
			ItemStackMorph morph = (ItemStackMorph) obj;

			result = result && this.lighting == morph.lighting;
		}

		return result;
	}

	@Override
	public void copy(AbstractMorph from)
	{
		super.copy(from);

		if (from instanceof ItemStackMorph)
		{
			ItemStackMorph morph = (ItemStackMorph) from;

			this.lighting = morph.lighting;
		}
	}

	@Override
	public void toNBT(NBTTagCompound tag)
	{
		super.toNBT(tag);

		if (!this.lighting)
		{
			tag.setBoolean("Lighting", this.lighting);
		}
	}

	@Override
	public void fromNBT(NBTTagCompound tag)
	{
		super.fromNBT(tag);

		if (tag.hasKey("Lighting"))
		{
			this.lighting = tag.getBoolean("Lighting");
		}
	}
}