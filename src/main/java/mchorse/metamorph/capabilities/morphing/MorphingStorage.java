package mchorse.metamorph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Morphing storage
 *
 * This class is responsible for saving IMorphing capability to... Hey, Houston,
 * where these data are getting saved? Basically, I don't know.
 *
 * Further research in Minecraft sources shows that capabilities are stored
 * in target's NBT (i.e. ItemStack's, TE's or Entity's NBT) in field "ForgeCaps."
 */
public class MorphingStorage implements IStorage<IMorphing>
{
    @Override
    public NBTBase writeNBT(Capability<IMorphing> capability, IMorphing instance, EnumFacing side)
    {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList acquired = new NBTTagList();
        NBTTagList favorites = new NBTTagList();

        if (instance.getCurrentMorph() != null)
        {
            NBTTagCompound morph = new NBTTagCompound();
            instance.getCurrentMorph().toNBT(morph);

            tag.setTag("Morph", morph);
        }

        tag.setTag("Morphs", acquired);
        tag.setTag("Favorites", favorites);

        for (AbstractMorph acquiredMorph : instance.getAcquiredMorphs())
        {
            NBTTagCompound acquiredTag = new NBTTagCompound();

            acquiredMorph.toNBT(acquiredTag);
            acquired.appendTag(acquiredTag);
        }

        for (Integer index : instance.getFavorites())
        {
            favorites.appendTag(new NBTTagInt(index.intValue()));
        }

        return tag;
    }

    @Override
    public void readNBT(Capability<IMorphing> capability, IMorphing instance, EnumFacing side, NBTBase nbt)
    {
        if (nbt instanceof NBTTagCompound)
        {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            NBTTagList acquired = tag.getTagList("Morphs", 10);
            NBTTagList favorites = tag.getTagList("Favorites", 3);
            NBTTagCompound morphTag = tag.getCompoundTag("Morph");

            if (!tag.hasNoTags())
            {
                instance.setCurrentMorph(MorphManager.INSTANCE.morphFromNBT(morphTag), null, true);
            }

            if (!acquired.hasNoTags())
            {
                List<AbstractMorph> acquiredMorphs = new ArrayList<AbstractMorph>();

                for (int i = 0; i < acquired.tagCount(); i++)
                {
                    NBTTagCompound acquiredTag = acquired.getCompoundTagAt(i);
                    AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(acquiredTag);

                    if (morph != null)
                    {
                        acquiredMorphs.add(morph);
                    }
                }

                instance.setAcquiredMorphs(acquiredMorphs);
            }

            if (!favorites.hasNoTags())
            {
                List<Integer> favoritesIndices = new ArrayList<Integer>();

                for (int i = 0; i < favorites.tagCount(); i++)
                {
                    favoritesIndices.add(favorites.getIntAt(i));
                }

                instance.setFavorites(favoritesIndices);
            }
        }
    }
}
