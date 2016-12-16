package mchorse.metamorph.api;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Morph factory
 * 
 * This factory interface will be responsible for providing different resources 
 * related to morphs like custom models, abilities, attacks or actions.
 */
public interface IMorphFactory
{
    /**
     * Register method
     * 
     * Register here everything that doesn't require stuff
     */
    public void register(MorphManager manager);

    /**
     * Register client method
     * 
     * Register here additional stuff that are related to client side
     */
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager);

    /**
     * Get all available morphs for this morphing factory
     */
    public void getMorphs(MorphList morphs);

    /**
     * Does this factory has morph by given name? 
     */
    public boolean hasMorph(String name);

    /**
     * Get a morph from NBT
     */
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag);
}