package mchorse.metamorph.api.morphs;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
     * Used for constructing an entity during loop 
     */
    public NBTTagCompound entityData;

    /**
     * Set entity for this morph
     */
    public void setEntity(EntityLivingBase entity)
    {
        this.entity = entity;

        entity.setHealth(entity.getMaxHealth());

        this.health = (int) entity.getMaxHealth();
        this.speed = 0.15F;
        this.hostile = entity instanceof EntityMob;

        if (entity instanceof EntityLiving)
        {
            ((EntityLiving) entity).setNoAI(true);
        }

        String name = EntityList.getEntityString(entity);
        int index = name.indexOf(".");

        if (index >= 0)
        {
            /* Category for third party mod mobs */
            this.category = name.substring(index);
        }
        else if (entity instanceof EntityAnimal)
        {
            this.category = "animal";
        }
        else if (entity instanceof EntityMob)
        {
            this.category = "hostile";
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
        if (entity == null)
        {
            Entity created = EntityList.createEntityByIDFromName(name, player.worldObj);

            created.deserializeNBT(entityData);
            this.setEntity((EntityLivingBase) created);

            if (player.worldObj.isRemote)
            {
                this.setupRenderer();
            }
        }

        /* Update entity */
        entity.onUpdate();
        entity.deathTime = 0;

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
        entity.renderYawOffset = player.renderYawOffset;

        entity.swingProgress = player.swingProgress;
        entity.limbSwing = player.limbSwing;
        entity.limbSwingAmount = player.limbSwingAmount;

        entity.prevPosX = player.prevPosX;
        entity.prevPosY = player.prevPosY;
        entity.prevPosZ = player.prevPosZ;

        entity.prevRotationYaw = player.prevRotationYaw;
        entity.prevRotationPitch = player.prevRotationPitch;
        entity.prevRotationYawHead = player.prevRotationYawHead;
        entity.prevRenderYawOffset = player.prevRenderYawOffset;

        entity.prevSwingProgress = player.prevSwingProgress;
        entity.prevLimbSwingAmount = player.prevLimbSwingAmount;

        entity.setSneaking(player.isSneaking());
        entity.setSprinting(player.isSprinting());
    }

    @SideOnly(Side.CLIENT)
    private void setupRenderer()
    {
        this.renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(this.entity);
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
        return entity == null ? this.entityData : entity.serializeNBT();
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setTag("EntityData", this.getEntityData());
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.entityData = tag.getCompoundTag("EntityData");
    }
}