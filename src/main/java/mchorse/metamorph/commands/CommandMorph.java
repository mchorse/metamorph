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
    public int getRequiredPermissionLevel()
    {
        /* Because /op command has the same level, and I trust it */
        return 3;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
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
                tag = JsonToNBT.getTagFromJson(args[2]);
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