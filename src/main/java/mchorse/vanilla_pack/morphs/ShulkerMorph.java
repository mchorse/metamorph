package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Shulker morph class
 * 
 * This method is responsible for freezing the player on the place where 
 * s/he was morphed and also disabling the movement of the body (shell).
 */
public class ShulkerMorph extends EntityMorph
{
    public BlockPos blockPos;

    @Override
    public void morph(EntityLivingBase target)
    {
        super.morph(target);

        this.blockPos = new BlockPos(target);
    }

    @Override
    public void demorph(EntityLivingBase target)
    {
        super.demorph(target);

        this.blockPos = null;
    }

    /**
     * Update the entity
     * 
     * This method is responsible holding the player in the player on 
     * morphed block position, and also aligns the body to the north.
     */
    @Override
    public void update(EntityLivingBase target)
    {
        if (this.blockPos != null)
        {
            target.motionX = target.motionY = target.motionZ = 0;
            target.setPosition(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5);
        }

        super.update(target);

        this.entity.renderYawOffset = this.entity.prevRenderYawOffset = 0;
    }

    @Override
    public AbstractMorph create()
    {
        return new ShulkerMorph();
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Pos"))
        {
            int[] pos = tag.getIntArray("Pos");

            if (pos.length == 3)
            {
                this.blockPos = new BlockPos(pos[0], pos[1], pos[2]);
            }
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.blockPos != null)
        {
            tag.setIntArray("Pos", new int[] {this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()});
        }
    }
}