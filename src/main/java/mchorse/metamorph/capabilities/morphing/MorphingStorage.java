package mchorse.metamorph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
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
        NBTTagList list = new NBTTagList();

        if (instance.getCurrentMorph() != null)
        {
            NBTTagCompound morph = new NBTTagCompound();
            instance.getCurrentMorph().toNBT(morph);

            tag.setTag("Morph", morph);
        }

        tag.setTag("Morphs", list);

        for (AbstractMorph acquiredMorph : instance.getAcquiredMorphs())
        {
            NBTTagCompound acquiredTag = new NBTTagCompound();

            acquiredMorph.toNBT(acquiredTag);
            list.appendTag(acquiredTag);
        }

        return tag;
    }

    @Override
    public void readNBT(Capability<IMorphing> capability, IMorphing instance, EnumFacing side, NBTBase nbt)
    {
        if (nbt instanceof NBTTagCompound)
        {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            NBTTagList list = tag.getTagList("Morphs", 10);
            NBTTagCompound morphTag = tag.getCompoundTag("Morph");

            if (!tag.hasNoTags())
            {
                instance.setCurrentMorph(MorphManager.INSTANCE.morphFromNBT(morphTag), null, true);
            }

            if (list.hasNoTags())
            {
                return;
            }

            List<AbstractMorph> acquiredMorphs = new ArrayList<AbstractMorph>();

            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound acquiredTag = list.getCompoundTagAt(i);

                acquiredMorphs.add(MorphManager.INSTANCE.morphFromNBT(acquiredTag));
            }

            instance.setAcquiredMorphs(acquiredMorphs);
        }
    }
}
