package mchorse.metamorph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.morph.MorphManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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
        String morph = MorphManager.INSTANCE.fromMorph(instance.getCurrentMorph());

        tag.setString("Morph", morph);
        tag.setTag("Morphs", list);

        for (String acquiredMorph : instance.getAcquiredMorphs())
        {
            System.out.println(acquiredMorph);

            list.appendTag(new NBTTagString(acquiredMorph));
        }

        return tag;
    }

    @Override
    public void readNBT(Capability<IMorphing> capability, IMorphing instance, EnumFacing side, NBTBase nbt)
    {
        if (nbt instanceof NBTTagCompound)
        {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            NBTTagList list = (NBTTagList) tag.getTag("Morphs");

            instance.setCurrentMorph(tag.getString("Morph"), null, true);

            if (list == null)
            {
                return;
            }

            List<String> acquiredMorphs = new ArrayList<String>();

            for (int i = 0; i < list.tagCount(); i++)
            {
                acquiredMorphs.add(list.getStringTagAt(i));
            }

            instance.setAcquiredMorphs(acquiredMorphs);
        }
    }
}
