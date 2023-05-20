package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ItemStackMorph extends AbstractMorph
{
    public boolean lighting = true;
    public boolean animated = false;
    public boolean realSize = false;

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
            result = result && this.animated == morph.animated;
            result = result && this.realSize == morph.realSize;
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
            this.animated = morph.animated;
            this.realSize = morph.realSize;
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
        if (this.animated)
        {
            tag.setBoolean("Animated", this.animated);
        }
        if (this.realSize)
        {
            tag.setBoolean("RealSize", this.realSize);
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
        if (tag.hasKey("Animated"))
        {
            this.animated = tag.getBoolean("Animated");
        }
        if (tag.hasKey("RealSize"))
        {
            this.realSize = tag.getBoolean("RealSize");
        }
    }
}