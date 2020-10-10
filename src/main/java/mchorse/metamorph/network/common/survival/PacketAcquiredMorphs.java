package mchorse.metamorph.network.common.survival;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

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
            AbstractMorph morph = MorphUtils.morphFromBuf(buf);

            if (morph != null)
            {
                this.morphs.add(morph);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.morphs.size());

        for (AbstractMorph morph : this.morphs)
        {
            MorphUtils.morphToBuf(buf, morph);
        }
    }
}
