package mchorse.metamorph.network.client.survival;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.survival.PacketMorphState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerMorphState extends ClientMessageHandler<PacketMorphState>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketMorphState message)
    {
        IMorphing capability = Morphing.get(player);
        AbstractMorph morph = capability.getCurrentMorph();

        if (morph instanceof EntityMorph)
        {
            Entity entity = ((EntityMorph) morph).getEntity(player.world);

            entity.setEntityId(message.entityID);
        }

        capability.setHasSquidAir(message.hasSquidAir);
        capability.setSquidAir(message.squidAir);
    }
}
