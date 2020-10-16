package mchorse.metamorph.api;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

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

    public boolean set(AbstractMorph morph)
    {
        if (this.morph == null || !this.morph.canMerge(morph))
        {
            if (this.morph != null && morph != null)
            {
                morph.afterMerge(this.morph);
            }

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

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Morph)
        {
            return Objects.equals(this.morph, ((Morph) obj).morph);
        }

        return super.equals(obj);
    }

    public AbstractMorph copy()
    {
        return MorphUtils.copy(this.morph);
    }

    public void copy(Morph morph)
    {
        this.set(morph.copy());
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