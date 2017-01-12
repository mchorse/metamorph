package mchorse.metamorph.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Packet favorite a morph
 */
public class PacketFavoriteMorph implements IMessage
{
    public int index;

    public PacketFavoriteMorph()
    {}

    public PacketFavoriteMorph(int index)
    {
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.index);
    }
}