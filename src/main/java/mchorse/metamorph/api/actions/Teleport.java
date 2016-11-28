package mchorse.metamorph.api.actions;

import mchorse.metamorph.api.IAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

/**
 * Teleport action
 * 
 * This action will teleport given player there where he's looking.
 * 
 * If the point where player is about to teleport have a block above, it will 
 * teleport player beside the block. Optionally, you can sneak to teleport 
 * beside the block.
 * 
 * Teleport action also has cooldown and limited distance to teleport in 
 * radius of 32 blocks.
 */
public class Teleport implements IAction
{
    @Override
    public void execute(EntityPlayer player)
    {
        float reachDistance = 32;

        Vec3d pos = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d vec = pos.addVector(look.xCoord * reachDistance, look.yCoord * reachDistance, look.zCoord * reachDistance);

        RayTraceResult result = player.worldObj.rayTraceBlocks(pos, vec, false, false, true);

        if (result != null && result.typeOfHit == Type.BLOCK)
        {
            BlockPos block = result.getBlockPos();

            if (player.getCooledAttackStrength(0.0F) < 1)
            {
                return;
            }

            if (player.isSneaking() || !player.worldObj.getBlockState(block.offset(EnumFacing.UP)).getBlock().equals(Blocks.AIR))
            {
                block = block.offset(result.sideHit);
            }

            double x = block.getX() + 0.5F;
            double y = block.getY() + 1.0F;
            double z = block.getZ() + 0.5F;

            player.worldObj.playSound((EntityPlayer) null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            player.setPositionAndUpdate(x, y, z);
            player.resetCooldown();
        }
    }
}