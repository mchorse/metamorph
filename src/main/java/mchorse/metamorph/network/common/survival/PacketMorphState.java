package mchorse.metamorph.network.common.survival;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMorphState implements IMessage
{
    public int entityID = 0;
    public boolean hasSquidAir = true;
    public int squidAir = 300;

    public PacketMorphState()
    {}

    public PacketMorphState(EntityPlayer player, IMorphing morphing)
    {
        if (morphing != null)
        {
            AbstractMorph morph = morphing.getCurrentMorph();

            if (morph instanceof EntityMorph)
            {
                entityID = ((EntityMorph) morph).getEntity(player.world).getEntityId();
            }

            this.hasSquidAir = morphing.getHasSquidAir();
            this.squidAir = morphing.getSquidAir();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.hasSquidAir = buf.readBoolean();
        this.squidAir = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.hasSquidAir);
        buf.writeInt(this.squidAir);
    }

}
