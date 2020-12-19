package mchorse.metamorph.api.morphs.utils;

import net.minecraft.nbt.NBTTagCompound;

public class Hitbox
{
	private static final Hitbox DEFAULT = new Hitbox();

	public boolean enabled;
	public float width;
	public float height;
	public float eye;
	public float sneakingHeight;

	public Hitbox()
	{
		this.reset();
	}

	public void reset()
	{
		this.enabled = false;
		this.width = 0.6F;
		this.height = 1.8F;
		this.eye = 0.9F;
		this.sneakingHeight = 1.65F;
	}

	public void copy(Hitbox hitbox)
	{
		this.enabled = hitbox.enabled;
		this.width = hitbox.width;
		this.height = hitbox.height;
		this.eye = hitbox.eye;
		this.sneakingHeight = hitbox.sneakingHeight;
	}

	public boolean isDefault()
	{
		return this.equals(DEFAULT);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Hitbox)
		{
			Hitbox hitbox = (Hitbox) obj;

			return this.enabled == hitbox.enabled
				&& this.width == hitbox.width
				&& this.height == hitbox.height
				&& this.eye == hitbox.eye
				&& this.sneakingHeight == hitbox.sneakingHeight;
		}

		return super.equals(obj);
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();

		if (this.enabled) tag.setBoolean("Enabled", true);
		if (this.width != 0.6F) tag.setFloat("Width", this.width);
		if (this.height != 0.6F) tag.setFloat("Height", this.height);
		if (this.eye != 0.6F) tag.setFloat("Eye", this.eye);
		if (this.sneakingHeight != 0.6F) tag.setFloat("Sneak", this.sneakingHeight);

		return tag;
	}

	public void fromNBT(NBTTagCompound tag)
	{
		if (tag.hasKey("Enabled"))
		{
			this.enabled = tag.getBoolean("Enabled");
		}

		if (tag.hasKey("Width"))
		{
			this.width = tag.getFloat("Width");
		}

		if (tag.hasKey("Height"))
		{
			this.height = tag.getFloat("Height");
		}

		if (tag.hasKey("Eye"))
		{
			this.eye = tag.getFloat("Eye");
		}

		if (tag.hasKey("Sneak"))
		{
			this.sneakingHeight = tag.getFloat("Sneak");
		}
	}
}