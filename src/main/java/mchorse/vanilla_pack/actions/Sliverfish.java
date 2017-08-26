package mchorse.vanilla_pack.actions;

import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.vanilla_pack.morphs.BlockMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

/**
 * Silverfish action
 * 
 * Turns silverfish into immovable block at which it was looking
 */
public class Sliverfish implements IAction
{
    @Override
    public void execute(EntityLivingBase target, AbstractMorph morph)
    {
        if (target.world.isRemote)
        {
            return;
        }

        float reachDistance = 5;

        Vec3d pos = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
        Vec3d look = target.getLook(1.0F);
        Vec3d vec = pos.addVector(look.xCoord * reachDistance, look.yCoord * reachDistance, look.zCoord * reachDistance);

        RayTraceResult result = target.world.rayTraceBlocks(pos, vec, false, false, true);

        if (result != null && result.typeOfHit == Type.BLOCK && target instanceof EntityPlayer)
        {
            BlockMorph block = new BlockMorph();

            block.blockPos = result.getBlockPos();
            block.block = target.world.getBlockState(block.blockPos);
            block.name = "metamorph.Block";

            target.world.setBlockToAir(block.blockPos);

            MorphAPI.morph((EntityPlayer) target, block, true);
        }
    }
}