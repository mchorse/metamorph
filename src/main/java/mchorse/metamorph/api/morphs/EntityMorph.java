package mchorse.metamorph.api.morphs;

import java.lang.reflect.Method;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.models.IHandProvider;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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

    /**
     * Did this instance already tried to setup first-person hands 
     */
    public boolean triedHands;

    /* Rendering */

    /**
     * Texture of the entity 
     */
    @SideOnly(Side.CLIENT)
    public ResourceLocation texture;

    /**
     * Cached model renderer for the left hand
     */
    @SideOnly(Side.CLIENT)
    public ModelRenderer leftHand;

    /**
     * Cached model renderer for the right hand
     */
    @SideOnly(Side.CLIENT)
    public ModelRenderer rightHand;

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        EntityLivingBase entity = this.getEntity(player.worldObj);

        if (entity.height > 2.5)
        {
            scale *= 2 / entity.height;
        }
        else if (this.name.equals("Guardian") && entity.height > 1.8)
        {
            scale *= 1 / entity.height;
        }

        GuiUtils.drawEntityOnScreen(x, y, scale, entity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        if (!this.triedHands && this.renderer != null)
        {
            this.setupTexture();
            this.setupHands();
            this.triedHands = true;
        }

        if (this.renderer == null || this.texture == null || this.leftHand == null || this.rightHand == null)
        {
            return true;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        ModelBase model = ((RenderLivingBase) this.renderer).getMainModel();

        model.swingProgress = 0.0F;
        model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, this.entity);

        GlStateManager.color(1.0F, 1.0F, 1.0F);

        if (hand.equals(EnumHand.MAIN_HAND))
        {
            float rax = this.rightHand.rotateAngleX;
            float rpx = this.rightHand.rotationPointX;
            float rpy = this.rightHand.rotationPointY;
            float rpz = this.rightHand.rotationPointZ;

            this.rightHand.rotateAngleX = 0;
            this.rightHand.rotationPointX = -6;
            this.rightHand.rotationPointY = 4;
            this.rightHand.rotationPointZ = 0;
            this.rightHand.render(0.0625F);

            this.rightHand.rotateAngleX = rax;
            this.rightHand.rotationPointX = rpx;
            this.rightHand.rotationPointY = rpy;
            this.rightHand.rotationPointZ = rpz;
        }
        else
        {
            float rax = this.leftHand.rotateAngleX;
            float rpx = this.leftHand.rotationPointX;
            float rpy = this.leftHand.rotationPointY;
            float rpz = this.leftHand.rotationPointZ;

            this.leftHand.rotateAngleX = 0;
            this.leftHand.rotationPointX = 6;
            this.leftHand.rotationPointY = 4;
            this.leftHand.rotationPointZ = 0;
            this.leftHand.render(0.0625F);

            this.leftHand.rotateAngleX = rax;
            this.leftHand.rotationPointX = rpx;
            this.leftHand.rotationPointY = rpy;
            this.leftHand.rotationPointZ = rpz;
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Render render = this.renderer;

        if (render == null)
        {
            this.getEntity(entity.worldObj);

            /* Make transformation seamless... */
            this.entity.rotationYaw = entity.rotationYaw;
            this.entity.rotationPitch = entity.rotationPitch;
            this.entity.rotationYawHead = entity.rotationYawHead;
            this.entity.renderYawOffset = entity.renderYawOffset;

            this.entity.prevRotationYaw = entity.prevRotationYaw;
            this.entity.prevRotationPitch = entity.prevRotationPitch;
            this.entity.prevRotationYawHead = entity.prevRotationYawHead;
            this.entity.prevRenderYawOffset = entity.prevRenderYawOffset;

            render = this.renderer;
        }

        if (render != null)
        {
            boolean isDragon = this.entity instanceof EntityDragon;

            if (isDragon)
            {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
            }

            if (render instanceof RenderLivingBase)
            {
                ModelBase model = ((RenderLivingBase) render).getMainModel();

                if (model instanceof ModelBiped)
                {
                    ((ModelBiped) model).isSneak = entity.isSneaking();
                }
            }

            render.doRender(this.entity, x, y, z, entityYaw, partialTicks);

            if (isDragon)
            {
                GlStateManager.popMatrix();
            }
        }
    }

    /* Other stuff */

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

        if (entity instanceof EntityLiving && !(entity instanceof EntityDragon))
        {
            ((EntityLiving) entity).setNoAI(true);
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
        this.updateSize(target, this.entity.width, this.entity.height);

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

        /* Now goes the code responsible for achieving somewhat riding 
         * support. This is ridiculous... */
        boolean targetRiding = target.isRiding();
        boolean entityRiding = entity.isRiding();

        if (targetRiding && !entityRiding)
        {
            entity.startRiding(new EntityPig(entity.worldObj));
        }
        else if (!targetRiding && entityRiding)
        {
            entity.dismountRidingEntity();
        }

        if (targetRiding)
        {
            /* One day, this cast is going to backfire, I'll wait for it... */
            EntityPig ride = (EntityPig) entity.getRidingEntity();
            Entity targetRide = target.getRidingEntity();

            if (ride == null || targetRide == null)
            {
                return;
            }

            ride.rotationYaw = targetRide.rotationYaw;
            ride.rotationPitch = targetRide.rotationPitch;

            ride.prevRotationYaw = targetRide.prevRotationYaw;
            ride.prevRotationPitch = targetRide.prevRotationPitch;

            if (targetRide instanceof EntityLivingBase)
            {
                ride.rotationYawHead = ((EntityLivingBase) targetRide).rotationYawHead;
                ride.renderYawOffset = ((EntityLivingBase) targetRide).renderYawOffset;

                ride.prevRotationYawHead = ((EntityLivingBase) targetRide).prevRotationYawHead;
                ride.prevRenderYawOffset = ((EntityLivingBase) targetRide).prevRenderYawOffset;
            }
        }
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

    /**
     * Setup renderer
     * 
     * This method is responsible for setting up any client side stuff like 
     * the renderer, texture of the entity and the "hands"
     */
    @SideOnly(Side.CLIENT)
    private void setupRenderer()
    {
        this.renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(this.entity);
    }

    /**
     * Get the renderer's texture
     * 
     * Very hard stuff are going on here
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setupTexture()
    {
        Class<Render> clazz = (Class<Render>) this.renderer.getClass();

        for (Method method : clazz.getDeclaredMethods())
        {
            Class[] args = method.getParameterTypes();

            boolean hasEntityArg = args.length == 1 && args[0].isAssignableFrom(Entity.class);
            boolean returnsRL = method.getReturnType().isAssignableFrom(ResourceLocation.class);

            if (hasEntityArg && returnsRL)
            {
                try
                {
                    method.setAccessible(true);
                    this.texture = (ResourceLocation) method.invoke(this.renderer, this.entity);
                }
                catch (Exception e)
                {
                    Metamorph.log("Failed to get texture of a morph '" + this.name + "'!");
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    /**
     * Setup this entity's hands
     * 
     * This guy is responsible for finding {@link ModelRenderer} in renderer's  
     * main model.
     * 
     * See {@link IHandProvider} for more information about support for hand 
     * rendering for third party support for your custom mob models who aren't 
     * {@link ModelBiped} or {@link ModelQuadruped}. 
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    private void setupHands()
    {
        ModelBase model = ((RenderLivingBase) this.renderer).getMainModel();

        model.setRotationAngles(0, 0, 0, 0, 0, 0.0625F, this.entity);

        if (model instanceof IHandProvider)
        {
            this.leftHand = ((IHandProvider) model).getLeft();
            this.rightHand = ((IHandProvider) model).getRight();
        }
        else if (model instanceof ModelBiped)
        {
            this.leftHand = ((ModelBiped) model).bipedLeftArm;
            this.rightHand = ((ModelBiped) model).bipedRightArm;
        }
        else if (model instanceof ModelQuadruped)
        {
            this.leftHand = ((ModelQuadruped) model).leg2;
            this.rightHand = ((ModelQuadruped) model).leg3;
        }
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

        return this.entity.width;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        if (this.entity == null)
        {
            this.setupEntity(target.worldObj);
        }

        return this.entity.height;
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