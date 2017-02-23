package mchorse.metamorph.network.common;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketBlacklist implements IMessage
{
    public Set<String> blacklist = new TreeSet<String>();

    public PacketBlacklist()
    {}

    public PacketBlacklist(Set<String> blacklist)
    {
        this.blacklist.addAll(blacklist);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            this.blacklist.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.blacklist.size());

        Iterator<String> it = this.blacklist.iterator();

        while (it.hasNext())
        {
            ByteBufUtils.writeUTF8String(buf, it.next());
        }
    }
}