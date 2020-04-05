package mchorse.metamorph.network.common.survival;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketIndex implements IMessage
{
	public int index;

	public PacketIndex()
	{}

	public PacketIndex(int index)
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
		buf.writeInt(index);
	}
}