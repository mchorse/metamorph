package mchorse.metamorph.network.common;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Acquired morphs packet 
 */
public class PacketAcquiredMorphs implements IMessage
{
    public List<String> morphs;

    public PacketAcquiredMorphs()
    {
        this.morphs = new ArrayList<String>();
    }

    public PacketAcquiredMorphs(List<String> morphs)
    {
        this.morphs = morphs;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            this.morphs.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.morphs.size());

        for (String string : this.morphs)
        {
            ByteBufUtils.writeUTF8String(buf, string);
        }
    }
}
