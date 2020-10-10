package mchorse.metamorph.network.client.survival;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.common.survival.PacketMorphPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerMorphPlayer extends ClientMessageHandler<PacketMorphPlayer>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketMorphPlayer message)
    {
        Entity entity = player.world.getEntityByID(message.id);
        IMorphing capability = entity.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability != null)
        {
            capability.setCurrentMorph(message.morph, (EntityPlayer) entity, true);
        }
    }
}
