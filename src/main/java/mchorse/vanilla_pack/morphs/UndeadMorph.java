package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Undead morph 
 * 
 * This morph is responsible for preventing hurt noises caused by sun 
 * allergy.
 */
public class UndeadMorph extends EntityMorph
{
    protected static final ItemStack HELMET;

    static
    {
        NBTTagCompound unbreakable = new NBTTagCompound();

        unbreakable.setBoolean("Unbreakable", true);

        HELMET = new ItemStack(Items.LEATHER_HELMET);
        HELMET.setTagCompound(unbreakable);
    }

    @Override
    protected void updateEntity(EntityLivingBase target)
    {
        boolean preventNoise = !target.isBurning();

        if (preventNoise)
        {
            this.entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, HELMET);
        }

        super.updateEntity(target);

        if (preventNoise)
        {
            this.entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
        }
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        UndeadMorph morph = new UndeadMorph();

        AbstractMorph.copyBase(this, morph);
        morph.entityData = this.entityData.copy();

        return morph;
    }
}