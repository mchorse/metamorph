package mchorse.metamorph.network.server.survival;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.api.events.MorphActionEvent;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.survival.PacketAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class ServerHandlerAction extends ServerMessageHandler<PacketAction>
{
    @Override
    public void run(EntityPlayerMP player, PacketAction message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null && capability.isMorphed() && !player.isSpectator())
        {
            AbstractMorph morph = capability.getCurrentMorph();

            morph.action(player);
            MinecraftForge.EVENT_BUS.post(new MorphActionEvent(player, morph.getSettings().action, morph));
        }
    }
}