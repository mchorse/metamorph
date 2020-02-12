package mchorse.metamorph.api;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Morph container 
 * 
 * This class is responsible for holding a morph, beside that it's also 
 * provides additional methods for cloning and other shit. It should be 
 * straightforward 
 */
public class Morph
{
    protected AbstractMorph morph;

    public Morph()
    {}

    public Morph(AbstractMorph morph)
    {
        this.morph = morph;
    }

    public boolean isEmpty()
    {
        return this.morph == null;
    }

    public boolean set(AbstractMorph morph, boolean isRemote)
    {
        if (this.morph == null || !this.morph.canMerge(morph, isRemote))
        {
            this.morph = morph;

            return true;
        }

        return false;
    }

    public void setDirect(AbstractMorph morph)
    {
        this.morph = morph;
    }

    public AbstractMorph get()
    {
        return this.morph;
    }

    public AbstractMorph clone(boolean isRemote)
    {
        return MorphUtils.clone(this.morph, isRemote);
    }

    public void copy(Morph morph, boolean isRemote)
    {
        this.set(morph.clone(isRemote), isRemote);
    }

    public void fromNBT(NBTTagCompound tag)
    {
        this.morph = MorphManager.INSTANCE.morphFromNBT(tag);
    }

    public NBTTagCompound toNBT()
    {
        return MorphUtils.toNBT(this.morph);
    }
}