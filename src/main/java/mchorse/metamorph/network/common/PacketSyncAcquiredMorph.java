package mchorse.metamorph.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.morphs.AbstractMorph;

public class PacketSyncAcquiredMorph extends PacketMorph
{
	public int index;

	public PacketSyncAcquiredMorph()
	{}

	public PacketSyncAcquiredMorph(AbstractMorph morph, int index)
	{
		super(morph);
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		this.index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(this.index);
	}
}