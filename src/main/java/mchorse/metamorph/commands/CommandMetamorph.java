package mchorse.metamorph.commands;

import java.util.List;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketBlacklist;
import mchorse.metamorph.network.common.PacketSettings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Command /metamorph
 * 
 * This command allows to manage
 */
public class CommandMetamorph extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "metamorph";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "metamorph.commands.metamorph";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        if (args.length >= 1)
        {
            String action = args[0];

            if (action.equals("reload") && args.length >= 2)
            {
                this.reload(args[1]);
            }
        }
    }

    /**
     * Reload something (blacklist or morph configuration) 
     */
    private void reload(String string)
    {
        /* Reload blacklist */
        if (string.equals("blacklist"))
        {
            MorphUtils.loadBlacklist(MorphManager.INSTANCE, Metamorph.proxy.blacklist);
            MorphManager.INSTANCE.setActiveBlacklist(MorphManager.INSTANCE.blacklist);

            this.broadcastPacket(new PacketBlacklist(MorphManager.INSTANCE.blacklist));
        }
        else if (string.equals("morphs"))
        {
            /* Reload morph config */
            MorphUtils.loadMorphSettings(MorphManager.INSTANCE, Metamorph.proxy.morphs);
            MorphManager.INSTANCE.setActiveSettings(MorphManager.INSTANCE.settings);

            this.broadcastPacket(new PacketSettings(MorphManager.INSTANCE.settings));
        }
    }

    /**
     * Broadcast a packet to all players 
     */
    private void broadcastPacket(IMessage packet)
    {
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                Dispatcher.sendTo(packet, player);
            }
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "reload");
        }

        if (args.length == 2)
        {
            if (args[0].equals("reload"))
            {
                return getListOfStringsMatchingLastWord(args, "blacklist", "morphs");
            }
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}