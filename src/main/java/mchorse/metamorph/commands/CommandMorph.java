package mchorse.metamorph.commands;

import java.util.List;

import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * Command /morph
 * 
 * This command is responsible for morphing a player into given morph.
 */
public class CommandMorph extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "morph";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "metamorph.commands.morph";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);

        if (player == null)
        {
            throw new CommandException("metamorph.error.morph.no_player", args[0]);
        }

        NBTTagCompound tag = null;

        if (args.length >= 3)
        {
            try
            {
                tag = JsonToNBT.getTagFromJson(args[2]);
            }
            catch (Exception e)
            {}
        }

        if (tag == null)
        {
            tag = new NBTTagCompound();
        }

        tag.setString("Name", args[1]);

        System.out.println(tag.toString());

        MorphAPI.morph(player, MorphManager.INSTANCE.morphFromNBT(tag), true);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}