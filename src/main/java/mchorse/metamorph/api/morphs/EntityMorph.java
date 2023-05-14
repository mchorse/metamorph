package mchorse.metamorph.api.morphs;

import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.MorphSettings;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.entity.SoundHandler;
import mchorse.metamorph.util.InvokeUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Entity morph class
 */
public class EntityMorph extends AbstractMorph implements IBodyPartProvider
{
    /**
     * Target entity which is going to be used for nametag rendering
     */
    @SideOnly(Side.CLIENT)
    public static EntityLivingBase renderEntity;

    /**
     * Cache map for registered body part layers
     */
    @SideOnly(Side.CLIENT)
    public static Map<Render, LayerBodyPart> bodyPartMap;

    /**
     * Body part manager
     */
    public BodyPartManager parts = new BodyPartManager();

    /**
     * Entity used by this morph to power morphing
     */
    protected EntityLivingBase entity;

    /**
     * Used for constructing an entity during loop 
     */
    protected NBTTagCompound entityData;

    /**
     * Custom settings were generated by this morph 
     */
    @Deprecated
    protected boolean customSettings;

    /**
     * If the associated entity is being updated
     */
    protected boolean updatingEntity = false;

    /* Rendering */

    @SideOnly(Side.CLIENT)
    public RenderLivingBase renderer;

    /**
     * Did this instance already tried to setup first-person hands
     */
    @SideOnly(Side.CLIENT)
    public boolean triedHands;

    /**
     * Linked body part layer
     */
    @SideOnly(Side.CLIENT)
    public LayerBodyPart layer;

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

    @SideOnly(Side.CLIENT)
    public Map<String, ModelRenderer> limbs;

    public ResourceLocation userTexture;

    public float scale = 1F;

    @SideOnly(Side.CLIENT)
    private ITextureObject lastTexture;

    @Override
    public BodyPartManager getBodyPart()
    {
        return this.parts;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        String name = this.name;

        try
        {
            name = EntityList.getEntityString(this.getEntity(Minecraft.getMinecraft().world));
        }
        catch (Exception e)
        {}

        String key = "entity." + name + ".name";
        String result = I18n.format(key);

        return key.equals(result) ? name : result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        EntityLivingBase entity = this.getEntity(player.world);

        if (entity.height > 2)
        {
            scale *= 2 / entity.height;
        }
        else if (entity.height < 0.6)
        {
            scale *= 0.5 / entity.height;
        }

        if (this.name.equals("minecraft:ghast"))
        {
            scale = 5F;
        }
        else if (this.name.equals("minecraft:guardian") && entity.height > 1.8)
        {
            scale *= 1 / entity.height;
        }

        this.parts.initBodyParts();

        if (!this.parts.parts.isEmpty())
        {
            this.setupLimbs();
        }

        this.setupBodyPart();
        this.replaceUserTexture();

        GuiUtils.drawEntityOnScreen(x, y, scale, entity, alpha);

        this.restoreMobTexture();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        if (!getSettings().hands)
        {
            return true;
        }

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

        this.replaceUserTexture();
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        ModelBase model = this.renderer.getMainModel();

        model.swingProgress = 0.0F;
        model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, this.entity);

        GlStateManager.color(1.0F, 1.0F, 1.0F);

        if (hand.equals(EnumHand.MAIN_HAND))
        {
            float rax = this.rightHand.rotateAngleX;
            float ray = this.rightHand.rotateAngleY;
            float raz = this.rightHand.rotateAngleZ;
            float rpx = this.rightHand.rotationPointX;
            float rpy = this.rightHand.rotationPointY;
            float rpz = this.rightHand.rotationPointZ;

            this.rightHand.rotateAngleX = 0;
            this.rightHand.rotateAngleY = 0;
            this.rightHand.rotateAngleZ = 0;
            this.rightHand.rotationPointX = -6;
            this.rightHand.rotationPointY = 4;
            this.rightHand.rotationPointZ = 0;
            this.rightHand.render(0.0625F);

            this.rightHand.rotateAngleX = rax;
            this.rightHand.rotateAngleY = ray;
            this.rightHand.rotateAngleZ = raz;
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

        this.restoreMobTexture();

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity == null)
        {
            return;
        }

        RenderLivingBase render = this.renderer;

        if (render == null)
        {
            this.getEntity(entity.world);

            render = this.renderer;
        }

        if (render != null)
        {
            /* Make transformation seamless... */
            this.entity.rotationYaw = entity.rotationYaw;
            this.entity.rotationPitch = entity.rotationPitch;
            this.entity.rotationYawHead = entity.rotationYawHead;
            this.entity.renderYawOffset = entity.renderYawOffset;

            this.entity.prevRotationYaw = entity.prevRotationYaw;
            this.entity.prevRotationPitch = entity.prevRotationPitch;
            this.entity.prevRotationYawHead = entity.prevRotationYawHead;
            this.entity.prevRenderYawOffset = entity.prevRenderYawOffset;

            this.parts.initBodyParts();

            if (!this.parts.parts.isEmpty())
            {
                this.setupLimbs();
            }

            boolean wasSneak = false;

            ModelBase model = render.getMainModel();

            if (model instanceof ModelBiped)
            {
                wasSneak = ((ModelBiped) model).isSneak;
                ((ModelBiped) model).isSneak = entity.isSneaking();
            }

            renderEntity = entity;
            this.setupBodyPart();
            this.replaceUserTexture();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(this.scale, this.scale, this.scale);

            if (this.entity instanceof EntityDragon)
            {
                GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);

                Minecraft.getMinecraft().getRenderManager().renderEntity(this.entity, 0, 0, 0, entityYaw, partialTicks, false);
            }
            else
            {
                Minecraft.getMinecraft().getRenderManager().renderEntity(this.entity, 0, 0, 0, entityYaw, partialTicks, false);
            }

            GlStateManager.popMatrix();

            this.restoreMobTexture();

            if (model instanceof ModelBiped)
            {
                ((ModelBiped) model).isSneak = wasSneak;
            }

            renderEntity = null;
        }
    }
    
    /* Entity morph settings */

    /**
     * These are settings that are defined from the morph entity.
     * They have lower priority than activeSettings.
     */
    protected MorphSettings entitySettings = null;
    
    protected void setEntitySettings(MorphSettings entitySettings) {
        this.entitySettings = entitySettings;
        this.needSettingsUpdate = true;
    }
    
    @Override
    public void initializeSettings()
    {
        if (!this.needSettingsUpdate)
        {
            return;
        }

        this.settings = MorphSettings.DEFAULT_MORPHED.copy();
        
        if (this.entitySettings != null)
        {
            this.settings.applyOverrides(this.entitySettings);
        }
        
        if (this.activeSettings != null)
        {
            this.settings.applyOverrides(this.activeSettings);
        }
        
        finishInitializingSettings();
    }

    @SideOnly(Side.CLIENT)
    protected void setupBodyPart()
    {
        if (this.layer == null)
        {
            return;
        }

        this.layer.morph = this;
    }

    @SideOnly(Side.CLIENT)
    protected void renderBodyParts(EntityLivingBase target, float partialTicks)
    {
        GlStateManager.pushMatrix();

        final float scale = 1 / 16F;

        for (BodyPart part : this.parts.parts)
        {
            for (Map.Entry<String, ModelRenderer> entry : this.limbs.entrySet())
            {
                if (entry.getKey().equals(part.limb))
                {
                    GlStateManager.pushMatrix();
                    entry.getValue().postRender(scale);
                    part.render(this, target, partialTicks);
                    GlStateManager.popMatrix();

                    break;
                }
            }
        }

        GlStateManager.popMatrix();
    }

    /**
     * This is pretty ugly, but it's the only way to replace entity's textures...
     */
    @SideOnly(Side.CLIENT)
    private void replaceUserTexture()
    {
        if (this.userTexture == null)
        {
            return;
        }

        if (this.texture == null)
        {
            this.setupTexture();
        }

        if (this.texture != null)
        {
            if (this.userTexture.equals(this.texture))
            {
                return;
            }

            TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
            Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(textureManager);

            if (map != null)
            {
                ITextureObject object = map.get(this.userTexture);

                if (object == null)
                {
                    textureManager.bindTexture(this.userTexture);
                    object = map.get(this.userTexture);
                }

                if (object != null)
                {
                    this.lastTexture = map.get(this.texture);

                    if (this.lastTexture == null)
                    {
                        textureManager.bindTexture(this.texture);
                        this.lastTexture = map.get(this.texture);
                    }

                    if (this.lastTexture != null)
                    {
                        map.put(this.texture, object);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void restoreMobTexture()
    {
        if (this.lastTexture != null)
        {
            TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
            Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(textureManager);

            map.put(this.texture, this.lastTexture);

            this.lastTexture = null;
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
        entity.noClip = true;
        entity.setAlwaysRenderNameTag(true);

        if (entity instanceof EntityLiving)
        {
            ((EntityLiving) entity).setLeftHanded(false);
        }

        MorphSettings entitySettings = new MorphSettings();
        entitySettings.health = (int)entity.getMaxHealth();
        entitySettings.hostile = entity instanceof EntityMob || entity instanceof EntityAnimal;
        IAttributeInstance speedAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        if (speedAttribute != null)
        {
            // By vanilla convention, mob movement speeds tend to be 2.5x
            // what the equivalent player speed would be.
            // Squids and players being the major exceptions.

            if ((entity instanceof EntityWaterMob))
            {
                // Check for EntityWaterMob rather than EntitySquid,
                // as EntityWaterMob would be deleted in an entity class refactor.
                entitySettings.speed = 0.1F;
            }
            else if (entity instanceof EntityPlayer)
            {
                entitySettings.speed = (float)speedAttribute.getBaseValue();
            }
            else
            {
                entitySettings.speed = 0.4F * (float)speedAttribute.getBaseValue();
            }
        }

        setEntitySettings(entitySettings);

        if (entity instanceof EntityLiving && !(entity instanceof EntityDragon))
        {
            ((EntityLiving) entity).setNoAI(true);
        }

        if (entity instanceof EntityAgeable && !entity.world.isRemote)
        {
            ((EntityAgeable) entity).setScaleForAge(entity.isChild());
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
    public void update(EntityLivingBase target)
    {
        if (entity == null)
        {
            this.setupEntity(target.world);
        }

        /* Update entity */
        this.updatingEntity = true;
        this.entity.setEntityInvulnerable(true);
        this.updateEntity(target);
        this.updatingEntity = false;
        this.entity.deathTime = target.deathTime;
        this.entity.hurtTime = target.hurtTime;

        if (this.entity instanceof EntityRabbit)
        {
            if (target.ticksExisted % 10 == 0 && target.limbSwingAmount > 0.4)
            {
                ((EntityRabbit) this.entity).startJumping();
            }
        }

        /* Update player */
        super.update(target);

        /* Update entity's inventory */
        if (target.world.isRemote)
        {
            int i = 0;

            for (ItemStack stack : target.getEquipmentAndArmor())
            {
                this.entity.setItemStackToSlot(EntityUtils.slotForIndex(i), stack);

                i++;
            }

            entity.setInvisible(target.isInvisible());
        }

        /* Injecting player's properties */
        this.entity.setPosition(target.posX, target.posY, target.posZ);

        this.entity.lastTickPosX = target.lastTickPosX;
        this.entity.lastTickPosY = target.lastTickPosY;
        this.entity.lastTickPosZ = target.lastTickPosZ;

        this.entity.prevPosX = target.prevPosX;
        this.entity.prevPosY = target.prevPosY;
        this.entity.prevPosZ = target.prevPosZ;

        this.entity.rotationYaw = target.rotationYaw;
        this.entity.rotationPitch = target.rotationPitch;
        this.entity.rotationYawHead = target.rotationYawHead;
        this.entity.renderYawOffset = target.renderYawOffset;

        this.entity.motionX = target.motionX;
        this.entity.motionY = target.motionY;
        this.entity.motionZ = target.motionZ;

        this.entity.isSwingInProgress = target.isSwingInProgress;
        this.entity.swingProgress = target.swingProgress;
        this.entity.limbSwing = target.limbSwing;
        this.entity.limbSwingAmount = target.limbSwingAmount;

        this.entity.prevRotationYaw = target.prevRotationYaw;
        this.entity.prevRotationPitch = target.prevRotationPitch;
        this.entity.prevRotationYawHead = target.prevRotationYawHead;
        this.entity.prevRenderYawOffset = target.prevRenderYawOffset;

        this.entity.prevSwingProgress = target.prevSwingProgress;
        this.entity.prevLimbSwingAmount = target.prevLimbSwingAmount;
        this.entity.swingingHand = target.swingingHand;

        if (this.entity instanceof EntityLiving)
        {
            ((EntityLiving) this.entity).setLeftHanded(target.getPrimaryHand() == EnumHandSide.LEFT);
        }

        if (target instanceof EntityPlayer && ((EntityPlayer) target).isCreative())
        {
            this.entity.fallDistance = 0;
        }
        else
        {
            this.entity.fallDistance = target.fallDistance;
        }

        this.entity.setSneaking(target.isSneaking());
        this.entity.setSprinting(target.isSprinting());
        this.entity.onGround = target.onGround;
        this.entity.isAirBorne = target.isAirBorne;
        this.entity.ticksExisted = target.ticksExisted;
        /* Fighting with death of entities like zombies */
        this.entity.setHealth(target.getHealth());

        if (target instanceof EntityPlayer)
        {
            IMorphing cap = Morphing.get((EntityPlayer) target);

            if (cap != null)
            {
                this.entity.setAir(cap.getHasSquidAir() ? cap.getSquidAir() : target.getAir());
            }
        }

        /* Now goes the code responsible for achieving somewhat riding 
         * support. This is ridiculous... */
        boolean targetRiding = target.isRiding();
        boolean entityRiding = this.entity.isRiding();

        if (targetRiding && !entityRiding)
        {
            this.entity.startRiding(new EntityPig(this.entity.world));
        }
        else if (!targetRiding && entityRiding)
        {
            this.entity.dismountRidingEntity();
        }

        if (targetRiding)
        {
            /* One day, this cast is going to backfire, I'll wait for it... */
            EntityPig ride = (EntityPig) this.entity.getRidingEntity();
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
                EntityLivingBase trr = (EntityLivingBase) targetRide;

                ride.rotationYawHead = trr.rotationYawHead;
                ride.renderYawOffset = trr.renderYawOffset;

                ride.prevRotationYawHead = trr.prevRotationYawHead;
                ride.prevRenderYawOffset = trr.prevRenderYawOffset;
            }
            else
            {
                ride.rotationYawHead = target.rotationYawHead;
                ride.renderYawOffset = target.renderYawOffset;

                ride.prevRotationYawHead = target.prevRotationYawHead;
                ride.prevRenderYawOffset = target.prevRenderYawOffset;
            }
        }

        if (this.entity instanceof EntityHorse)
        {
            EntityHorse horse = (EntityHorse) this.entity;

            horse.setHorseSaddled(this.entityData.hasKey("SaddleItem"));
        }

        this.parts.updateBodyLimbs(this, target);
    }

    protected void updateEntity(EntityLivingBase target)
    {
        if (getSettings().updates)
        {
            if (!Metamorph.showMorphIdleSounds.get())
            {
                this.entity.setSilent(true);
            }

            this.entity.onUpdate();
            this.entity.setSilent(false);
        }
    }

    public boolean isUpdatingEntity()
    {
        return this.updatingEntity;
    }

    @Override
    protected void updateUserHitbox(EntityLivingBase target)
    {
        float width = this.entity.width;
        float height = this.entity.height;

        boolean isAnimalChild = this.entity instanceof EntityAgeable && this.entityData.getInteger("Age") < 0;

        /* Because Minecraft is shit at syncing data!
         *
         * The problem is that Minecraft changes to correct size of baby
         * animals on the client, but on the server it doesn't change anything
         * thus I have to rely on proivded NBT data for figuring out if an
         * animal entity is being a baby */
        if (!target.world.isRemote && isAnimalChild)
        {
            width *= 0.5;
            height *= 0.5;
        }

        this.updateSize(target, width, height);
    }

    /**
     * Setup entity
     * 
     * This is responsible for setting the entity
     */
    public void setupEntity(World world)
    {
        EntityLivingBase created = (EntityLivingBase) EntityList.createEntityByIDFromName(new ResourceLocation(this.name), world);

        try
        {
            created.deserializeNBT(this.entityData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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
    protected void setupRenderer()
    {
        Render renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(this.entity);

        if (renderer instanceof RenderLivingBase)
        {
            this.renderer = (RenderLivingBase) renderer;
            ModelBase model = this.renderer.getMainModel();

            if (this.entity != null && model instanceof ModelBiped || model instanceof ModelQuadruped)
            {
                // Entity settings should have been defined when setEntity(...) was called
                assert(this.entitySettings != null);
                MorphSettings entitySettings = this.entitySettings != null ? this.entitySettings : new MorphSettings();
                entitySettings.hands = true;
                setEntitySettings(entitySettings);
            }

            if (bodyPartMap == null)
            {
                bodyPartMap = new HashMap<Render, LayerBodyPart>();
            }

            this.layer = bodyPartMap.get(renderer);

            if (this.layer == null)
            {
                bodyPartMap.put(this.renderer, this.layer = new LayerBodyPart());
                this.renderer.addLayer(layer);
            }
        }
    }

    /**
     * Get the renderer's texture
     * 
     * Very hard stuff are going on here
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void setupTexture()
    {
        if (this.texture != null)
        {
            return;
        }

        Class<RenderLivingBase> clazz = (Class<RenderLivingBase>) this.renderer.getClass();

        for (Method method : clazz.getDeclaredMethods())
        {
            Class[] args = method.getParameterTypes();

            boolean hasEntityArg = args.length == 1 && Entity.class.isAssignableFrom(args[0]);
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
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    protected void setupHands()
    {
        ModelBase model = this.renderer.getMainModel();

        model.setRotationAngles(0, 0, 0, 0, 0, 0.0625F, this.entity);

        if (model instanceof ModelBiped)
        {
            this.leftHand = ((ModelBiped) model).bipedLeftArm;
            this.rightHand = ((ModelBiped) model).bipedRightArm;
        }
        else if (model instanceof ModelQuadruped)
        {
            this.leftHand = ((ModelQuadruped) model).leg2;
            this.rightHand = ((ModelQuadruped) model).leg3;
        }
        else
        {
            /* For anything else, pretty bad algorithm */
            List<ModelRenderer> left = new ArrayList<ModelRenderer>();
            List<ModelRenderer> right = new ArrayList<ModelRenderer>();

            left.addAll(model.boxList);
            right.addAll(model.boxList);

            Collections.sort(left, new Comparator<ModelRenderer>()
            {
                @Override
                public int compare(ModelRenderer a, ModelRenderer b)
                {
                    return (int) (a.rotationPointX - b.rotationPointX < 0 ? Math.floor(a.rotationPointX - b.rotationPointX) : Math.ceil(a.rotationPointX - b.rotationPointX));
                }
            });

            Collections.sort(right, new Comparator<ModelRenderer>()
            {
                @Override
                public int compare(ModelRenderer a, ModelRenderer b)
                {
                    return (int) (b.rotationPointX - a.rotationPointX < 0 ? Math.floor(b.rotationPointX - a.rotationPointX) : Math.ceil(b.rotationPointX - a.rotationPointX));
                }
            });

            this.leftHand = left.isEmpty() ? null : left.get(0);
            this.rightHand = right.isEmpty() ? null : right.get(0);
        }
    }

    @SideOnly(Side.CLIENT)
    public void setupLimbs()
    {
        if (this.limbs != null)
        {
            return;
        }

        ModelBase model = this.renderer.getMainModel();

        /* Setup model limbs map */
        this.limbs = new HashMap<String, ModelRenderer>();

        Field[] fields = FieldUtils.getAllFields(model.getClass());

        for (Field field : fields)
        {
            field.setAccessible(true);

            if (field.getType().isAssignableFrom(ModelRenderer.class))
            {
                try
                {
                    ModelRenderer renderer = (ModelRenderer) field.get(model);

                    this.limbs.put(field.getName(), renderer);
                }
                catch (Exception e)
                {}
            }
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
            EntityMorph morph = (EntityMorph) obj;
            boolean theSame = EntityUtils.compareData(morph.entityData, this.entityData);

            result = result && theSame;
            result = result && Objects.equals(morph.parts, this.parts);
            result = result && morph.scale == this.scale;
            result = result && Objects.equals(morph.userTexture, this.userTexture);
        }

        return result;
    }

    @Override
    public void reset()
    {
        this.parts.reset();
        this.resetEntity();
        this.entityData = null;
        this.scale = 1F;
        this.userTexture = null;

        super.reset();
    }

    public void resetEntity()
    {
        if (this.entity != null)
        {
            if (this.entity.world.isRemote)
            {
                this.renderer = null;
                this.triedHands = false;
            }

            this.entity = null;
            setEntitySettings(null);
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new EntityMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof EntityMorph)
        {
            EntityMorph morph = (EntityMorph) from;
            
            this.entitySettings = this.entitySettings != null ? this.entitySettings.copy() : null;
            this.entityData = morph.entityData != null ? morph.entityData.copy() : null;
            this.parts.copy(morph.parts);
            this.scale = morph.scale;
            this.userTexture = RLUtils.clone(morph.userTexture);
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        if (this.entity == null)
        {
            this.setupEntity(target.world);
        }

        return this.entity.width;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        if (this.entity == null)
        {
            this.setupEntity(target.world);
        }

        return this.entity.height;
    }

    @Override
    public SoundEvent getHurtSound(EntityLivingBase target, DamageSource damageSource)
    {
        EntityLivingBase entity = this.getEntity(target.world);

        try
        {
            Method methodHurtSound = InvokeUtil.getPrivateMethod(entity.getClass(), EntityLivingBase.class, SoundHandler.GET_HURT_SOUND.getName(), DamageSource.class);
            SoundEvent hurtSound = (SoundEvent) methodHurtSound.invoke(entity, damageSource);
            if (hurtSound == null)
            {
                hurtSound = SoundHandler.NO_SOUND;
            }
            return hurtSound;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SoundEvent getDeathSound(EntityLivingBase target)
    {
        EntityLivingBase entity = this.getEntity(target.world);
        try
        {
            Method methodDeathSound = InvokeUtil.getPrivateMethod(entity.getClass(), EntityLivingBase.class, SoundHandler.GET_DEATH_SOUND.getName());
            SoundEvent deathSound = (SoundEvent) methodDeathSound.invoke(entity);
            if (deathSound == null)
            {
                deathSound = SoundHandler.NO_SOUND;
            }
            return deathSound;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean hasCustomStepSound(EntityLivingBase target)
    {
        return true;
    }

    @Override
    public void playStepSound(EntityLivingBase target)
    {
        EntityLivingBase entity = this.getEntity(target.world);
        try
        {
            Method methodPlayStep = InvokeUtil.getPrivateMethod(entity.getClass(), Entity.class, SoundHandler.PLAY_STEP_SOUND.getName(), BlockPos.class, Block.class);

            int x = MathHelper.floor(entity.posX);
            int y = MathHelper.floor(entity.posY - 0.20000000298023224D);
            int z = MathHelper.floor(entity.posZ);
            BlockPos pos = new BlockPos(x, y, z);
            Block block = entity.world.getBlockState(pos).getBlock();

            methodPlayStep.invoke(entity, pos, block);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeDimension(EntityPlayer player, int oldDim, int currentDim)
    {
        if (this.entity != null)
        {
            this.entity.world = player.world;
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setTag("EntityData", this.entityData);
        tag.setFloat("Scale", this.scale);

        if (this.userTexture != null)
        {
            tag.setTag("Texture", RLUtils.writeNbt(this.userTexture));
        }

        NBTTagList bodyParts = this.parts.toNBT();

        if (bodyParts != null)
        {
            tag.setTag("BodyParts", bodyParts);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.entityData = tag.getCompoundTag("EntityData");

        if (tag.hasKey("Scale"))
        {
            this.scale = tag.getFloat("Scale");
        }

        if (tag.hasKey("Texture"))
        {
            this.userTexture = RLUtils.create(tag.getTag("Texture"));
        }

        if (tag.hasKey("BodyParts", 9))
        {
            this.parts.fromNBT(tag.getTagList("BodyParts", 10));
        }
    }

    @SideOnly(Side.CLIENT)
    public static class LayerBodyPart implements LayerRenderer<EntityLivingBase>
    {
        public EntityMorph morph;

        @Override
        public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
        {
            if (this.morph != null)
            {
                if (entity.isSneaking())
                {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                this.morph.renderBodyParts(entity, 1F);
            }
        }

        @Override
        public boolean shouldCombineTextures()
        {
            return false;
        }
    }
}
