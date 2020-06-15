package mchorse.metamorph.network.common.creative;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorph implements IMessage
{
    public AbstractMorph morph;

    public PacketMorph()
    {}

    public PacketMorph(AbstractMorph morph)
    {
        this.morph = morph;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.morph = MorphManager.INSTANCE.morphFromNBT(NBTUtils.readInfiniteTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        MorphUtils.morphToBuf(buf, this.morph);
    }
}