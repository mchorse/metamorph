package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ItemStackMorph extends AbstractMorph
{
    public boolean lighting = true;
    public boolean dropped = false;

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
            result = result && this.dropped == morph.dropped;
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
            this.dropped = morph.dropped;
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
        if (this.dropped)
        {
            tag.setBoolean("Dropped", this.dropped);
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
        if (tag.hasKey("Dropped"))
        {
            this.dropped = tag.getBoolean("Dropped");
        }
    }
}