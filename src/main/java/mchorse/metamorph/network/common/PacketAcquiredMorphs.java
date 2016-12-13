package mchorse.metamorph.network.common;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Acquired morphs packet 
 */
public class PacketAcquiredMorphs implements IMessage
{
    public List<AbstractMorph> morphs;

    public PacketAcquiredMorphs()
    {
        this.morphs = new ArrayList<AbstractMorph>();
    }

    public PacketAcquiredMorphs(List<AbstractMorph> morphs)
    {
        this.morphs = morphs;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            this.morphs.add(MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.morphs.size());

        for (AbstractMorph morph : this.morphs)
        {
            NBTTagCompound tag = new NBTTagCompound();

            morph.toNBT(tag);
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}
