package mchorse.metamorph.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphSettings;
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
    public String getName()
    {
        return "metamorph";
    }

    @Override
    public String getUsage(ICommandSender sender)
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
            throw new WrongUsageException(this.getUsage(sender));
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
            Set<String> blacklist = MorphUtils.reloadBlacklist();

            MorphManager.INSTANCE.setActiveBlacklist(blacklist);
            this.broadcastPacket(new PacketBlacklist(blacklist));
        }
        else if (string.equals("morphs"))
        {
            /* Reload morph config */
            Map<String, MorphSettings> settings = MorphUtils.reloadMorphSettings();

            MorphManager.INSTANCE.setActiveSettings(settings);
            this.broadcastPacket(new PacketSettings(settings));
        }
    }

    /**
     * Broadcast a packet to all players 
     */
    private void broadcastPacket(IMessage packet)
    {
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getOnlinePlayerNames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                Dispatcher.sendTo(packet, player);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
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

        return super.getTabCompletions(server, sender, args, pos);
    }
}