package mchorse.metamorph.network.common.creative;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.morphs.AbstractMorph;

/**
 * Acquire morph packet
 */
public class PacketAcquireMorph extends PacketMorph
{
    public boolean notify;

    public PacketAcquireMorph()
    {
        super();
    }

    public PacketAcquireMorph(AbstractMorph morph)
    {
        this(morph, true);
    }

    public PacketAcquireMorph(AbstractMorph morph, boolean notify)
    {
        super(morph);

        this.notify = notify;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.notify = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeBoolean(this.notify);
    }
}