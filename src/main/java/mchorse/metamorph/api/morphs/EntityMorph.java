package mchorse.metamorph.api.morphs;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Entity morph class
 * 
 * This morph class is based on 
 */
public class EntityMorph extends AbstractMorph
{
    /**
     * Entity used by this morph to power morphing
     */
    protected EntityLivingBase entity;

    /**
     * Set entity for this morph
     */
    public void setEntity(EntityLivingBase entity)
    {
        this.entity = entity;

        this.health = (int) entity.getMaxHealth();
        this.speed = (float) entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        this.hostile = entity instanceof EntityMob;

        if (entity instanceof EntityLiving)
        {
            ((EntityLiving) entity).setNoAI(true);
        }
    }

    /**
     * Get used entity of this morph 
     */
    public EntityLivingBase getEntity()
    {
        return this.entity;
    }

    @Override
    public void update(EntityPlayer player, IMorphing cap)
    {
        /* Update entity */
        entity.onUpdate();

        /* Update player */
        this.updateSize(player, entity.width, entity.height);

        super.update(player, cap);

        /* Injecting player's properties */
        entity.posX = player.posX;
        entity.posY = player.posY;
        entity.posZ = player.posZ;

        entity.motionX = player.motionX;
        entity.motionY = player.motionY;
        entity.motionZ = player.motionZ;

        entity.rotationYaw = player.rotationYaw;
        entity.rotationPitch = player.rotationPitch;
        entity.rotationYawHead = player.rotationYawHead;

        entity.setSneaking(player.isSneaking());
        entity.setSprinting(player.isSprinting());
        entity.setHealth(player.getHealth());
    }

    /**
     * Get entity serialized {@link NBTTagCompound}
     * 
     * This method is going to be used for saving entity state to morph 
     * capability. 
     */
    @Override
    public NBTTagCompound getEntityData()
    {
        return entity.serializeNBT();
    }
}