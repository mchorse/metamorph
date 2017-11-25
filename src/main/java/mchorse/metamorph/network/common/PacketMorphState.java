package mchorse.metamorph.network.common;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorphState implements IMessage
{
    public boolean hasSquidAir = true;
    public int squidAir = 300;
    
    public PacketMorphState()
    {}
    
    public PacketMorphState(IMorphing morphing)
    {
        if (morphing != null)
        {
            this.hasSquidAir = morphing.getHasSquidAir();
            this.squidAir = morphing.getSquidAir();
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.hasSquidAir = buf.readBoolean();
        this.squidAir = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.hasSquidAir);
        buf.writeInt(this.squidAir);
    }

}
