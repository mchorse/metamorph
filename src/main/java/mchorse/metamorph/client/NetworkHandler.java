package mchorse.metamorph.client;

import java.nio.charset.Charset;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphSettings;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom payload network hander
 * 
 * This network handler allows plugins to give morphs to players
 */
@SideOnly(Side.CLIENT)
public class NetworkHandler
{
    /**
     * This handler handles custom payload messages 
     */
    @SubscribeEvent
    public void onCustomMessage(ClientCustomPacketEvent event)
    {
        try
        {
            PacketBuffer buffer = (PacketBuffer) event.getPacket().payload();
            byte[] array = new byte[buffer.capacity()];

            for (int i = 0, c = array.length; i < c; i++)
            {
                array[i] = buffer.readByte();
            }

            String data = new String(array, Charset.forName("UTF-8")).trim();
            String[] args = data.split(" ");

            if (args.length >= 1)
            {
                Minecraft.getMinecraft().addScheduledTask(new MorphRunnable(args));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Morph runnable
     * 
     * This class is responsible for morphing a player into a morph or 
     * demorph him based on the arguments passed in the payload packet.
     */
    public static class MorphRunnable implements Runnable
    {
        public String[] args;

        public MorphRunnable(String[] args)
        {
            this.args = args;
        }

        @Override
        public void run()
        {
            EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByName(this.args[0]);

            if (player == null)
            {
                return;
            }

            if (this.args.length >= 3)
            {
                String data = this.args[2];

                for (int i = 3; i < this.args.length; i++)
                {
                    data += " " + this.args[i];
                }

                try
                {
                    NBTTagCompound tag = JsonToNBT.getTagFromJson(data);
                    tag.setString("Name", this.args[1]);
                    this.morph(tag, player);
                }
                catch (Exception e)
                {}
            }
            else if (this.args.length == 2)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("Name", this.args[1]);
                this.morph(tag, player);
            }
            else if (this.args.length == 1)
            {
                Morphing.get(player).setCurrentMorph(null, player, true);
            }
        }

        private void morph(NBTTagCompound tag, EntityPlayer player)
        {
            AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tag);

            if (morph != null)
            {
                /* No fancy stuff or actions */
                morph.forceSettings(MorphSettings.DEFAULT);

                Morphing.get(player).setCurrentMorph(morph, player, true);
            }
        }
    }
}