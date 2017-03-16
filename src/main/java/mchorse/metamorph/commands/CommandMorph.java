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
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Command /morph
 * 
 * This command is responsible for morphing a player into given morph.
 */
public class CommandMorph extends CommandBase
{
    @Override
    public String getName()
    {
        return "morph";
    }

    @Override
    public String getUsage(ICommandSender sender)
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
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        Entity entity = getEntity(server, sender, args[0]);

        if (!(entity instanceof EntityPlayer))
        {
            throw new CommandException("metamorph.error.morph.not_player", entity.getDisplayName());
        }

        EntityPlayer player = (EntityPlayer) entity;

        if (args.length < 2)
        {
            MorphAPI.demorph(player);
            sender.sendMessage(new TextComponentTranslation("metamorph.success.demorph", args[0]));
        }
        else
        {
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
            sender.sendMessage(new TextComponentTranslation("metamorph.success.morph", args[0], args[1]));
        }
    }

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