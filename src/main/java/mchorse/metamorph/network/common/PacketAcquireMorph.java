package mchorse.metamorph.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Acquire morph packet
 */
public class PacketAcquireMorph implements IMessage
{
    public String morph;

    public PacketAcquireMorph()
    {}

    public PacketAcquireMorph(String morph)
    {
        this.morph = morph;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.morph = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.morph);
    }
}