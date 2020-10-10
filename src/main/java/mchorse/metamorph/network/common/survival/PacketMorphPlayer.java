package mchorse.metamorph.network.common.survival;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorphPlayer implements IMessage
{
    public int id;
    public AbstractMorph morph;

    public PacketMorphPlayer()
    {}

    public PacketMorphPlayer(int id, AbstractMorph morph)
    {
        this.id = id;
        this.morph = morph;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.id = buf.readInt();
        this.morph = MorphUtils.morphFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.id);
        MorphUtils.morphToBuf(buf, this.morph);
    }
}