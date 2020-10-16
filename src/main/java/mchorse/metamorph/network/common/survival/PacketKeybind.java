package mchorse.metamorph.network.common.survival;

import io.netty.buffer.ByteBuf;

public class PacketKeybind extends PacketIndex
{
	public int keybind;

	public PacketKeybind()
	{
		super();
	}

	public PacketKeybind(int index, int keybind)
	{
		super(index);

		this.keybind = keybind;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		this.keybind = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(this.keybind);
	}
}