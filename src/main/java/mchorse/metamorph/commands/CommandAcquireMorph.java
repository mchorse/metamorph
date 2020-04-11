package mchorse.metamorph.commands;

import java.util.List;

import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Command /acquire_morph
 * 
 * This command is responsible for sending a morph to given player as an 
 * acquired morph.
 */
public class CommandAcquireMorph extends CommandBase
{
    @Override
    public String getName()
    {
        return "acquire_morph";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "metamorph.commands.acquire_morph";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        /* Because /op command has the same level, and I trust it */
        return 3;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender instanceof CommandBlockBaseLogic || super.checkPermission(server, sender);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        Entity entity = getEntity(server, sender, args[0]);

        if (!(entity instanceof EntityPlayer))
        {
            throw new CommandException("metamorph.error.morph.not_player", entity.getDisplayName());
        }

        EntityPlayer player = (EntityPlayer) entity;

        NBTTagCompound tag = null;

        if (args.length >= 3)
        {
            try
            {
                tag = JsonToNBT.getTagFromJson(CommandMorph.mergeArgs(args, 2));
            }
            catch (Exception e)
            {
                throw new CommandException("metamorph.error.morph.nbt", e.getMessage());
            }
        }

        if (tag == null)
        {
            tag = new NBTTagCompound();
        }

        tag.setString("Name", args[1]);

        if (!MorphAPI.acquire(player, MorphManager.INSTANCE.morphFromNBT(tag)))
        {
            throw new CommandException("metamorph.error.acquire", args[1]);
        }

        if (sender.sendCommandFeedback())
        {
            sender.sendMessage(new TextComponentTranslation("metamorph.success.acquire", args[0], args[1]));
        }
    }

    /**
     * Provide completion for player usernames for first argument
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}
