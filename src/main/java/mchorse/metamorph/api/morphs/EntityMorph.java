package mchorse.metamorph.api.morphs;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
    protected NBTTagCompound entityData;

    public float width;

    public float height;

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        EntityLivingBase entity = this.getEntity(player.worldObj);

        if (entity.height > 2.5)
        {
            scale *= 2 / entity.height;
        }

        GuiUtils.drawEntityOnScreen(x, y, scale, entity);

        this.entity.ticksExisted++;

        if (entity.ticksExisted > 10000)
        {
            entity.ticksExisted = 0;
        }
    }

    /**
     * Set entity for this morph
     */
    public void setEntity(EntityLivingBase entity)
    {
        this.entity = entity;

        entity.setHealth(entity.getMaxHealth());

        if (this.health == 20)
        {
            this.health = (int) entity.getMaxHealth();
        }

        this.hostile = entity instanceof EntityMob;
        this.width = entity.width;
        this.height = entity.height;

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

        if (this.entityData == null)
        {
            this.entityData = EntityUtils.stripEntityNBT(this.entity.serializeNBT());
        }
    }

    /**
     * Get used entity of this morph 
     */
    public EntityLivingBase getEntity()
    {
        return this.entity;
    }

    /**
     * Get used entity of this morph, if there's no entity, just create it with 
     * provided world.
     */
    public EntityLivingBase getEntity(World world)
    {
        if (this.entity == null)
        {
            this.setupEntity(world);
        }

        return this.entity;
    }

    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        if (entity == null)
        {
            this.setupEntity(target.worldObj);
        }

        /* Update entity */
        entity.onUpdate();
        entity.deathTime = target.deathTime;
        entity.hurtTime = target.hurtTime;

        /* Update player */
        this.updateSize(target, this.width, this.height);

        super.update(target, cap);

        /* Update entity's inventory */
        if (target.worldObj.isRemote)
        {
            int i = 0;

            for (ItemStack stack : target.getEquipmentAndArmor())
            {
                entity.setItemStackToSlot(EntityUtils.slotForIndex(i), stack);

                i++;
            }

            entity.setInvisible(target.isInvisible());
        }

        /* Injecting player's properties */
        entity.posX = target.posX;
        entity.posY = target.posY;
        entity.posZ = target.posZ;

        entity.rotationYaw = target.rotationYaw;
        entity.rotationPitch = target.rotationPitch;

        entity.motionX = target.motionX;
        entity.motionY = target.motionY;
        entity.motionZ = target.motionZ;

        entity.rotationYawHead = target.rotationYawHead;
        entity.renderYawOffset = target.renderYawOffset;

        entity.swingProgress = target.swingProgress;
        entity.limbSwing = target.limbSwing;
        entity.limbSwingAmount = target.limbSwingAmount;

        entity.prevPosX = target.prevPosX;
        entity.prevPosY = target.prevPosY;
        entity.prevPosZ = target.prevPosZ;

        entity.prevRotationYaw = target.prevRotationYaw;
        entity.prevRotationPitch = target.prevRotationPitch;
        entity.prevRotationYawHead = target.prevRotationYawHead;
        entity.prevRenderYawOffset = target.prevRenderYawOffset;

        entity.prevSwingProgress = target.prevSwingProgress;
        entity.prevLimbSwingAmount = target.prevLimbSwingAmount;

        entity.setSneaking(target.isSneaking());
        entity.setSprinting(target.isSprinting());
        entity.onGround = target.onGround;
        entity.isAirBorne = target.isAirBorne;
        entity.ticksExisted = target.ticksExisted;
    }

    /**
     * Setup entity
     * 
     * This is responsible for setting the entity
     */
    public void setupEntity(World world)
    {
        EntityLivingBase created = (EntityLivingBase) EntityList.createEntityByIDFromName(name, world);

        created.deserializeNBT(this.entityData);
        created.deathTime = 0;
        created.hurtTime = 0;
        created.limbSwing = 0;
        created.setFire(0);

        this.setEntity(created);

        if (world.isRemote)
        {
            this.setupRenderer();
        }
    }

    @SideOnly(Side.CLIENT)
    private void setupRenderer()
    {
        this.renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(this.entity);
    }

    /**
     * Set entity data 
     */
    public void setEntityData(NBTTagCompound tag)
    {
        this.entityData = tag;
    }

    /**
     * Get entity serialized {@link NBTTagCompound}
     * 
     * This method is going to be used for saving entity state to morph 
     * capability. 
     */
    public NBTTagCompound getEntityData()
    {
        return this.entityData;
    }

    /**
     * Check if this and given EntityMorphs are equal 
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof EntityMorph)
        {
            boolean theSame = EntityUtils.compareData(((EntityMorph) obj).entityData, this.entityData);

            return result && theSame;
        }

        return result;
    }

    /**
     * Clone this {@link EntityMorph} 
     */
    @Override
    public AbstractMorph clone()
    {
        EntityMorph morph = new EntityMorph();

        morph.name = this.name;
        morph.category = this.category;

        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.entityData = this.entityData.copy();

        return morph;
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        if (this.entity == null)
        {
            this.setupEntity(target.worldObj);
        }

        return this.width;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        if (this.entity == null)
        {
            this.setupEntity(target.worldObj);
        }

        return this.height;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setTag("EntityData", this.entityData);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.entityData = tag.getCompoundTag("EntityData");
    }
}