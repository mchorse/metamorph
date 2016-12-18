package mchorse.metamorph.api.morphs;

import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for all different types of morphs
 * 
 * This is an abstract morph. It contains all needed properties for a basic 
 * morph such as abilities, action, attack, health, speed and hotstyle flag.
 */
public abstract class AbstractMorph
{
    /* Abilities */

    /**
     * Morph's abilities 
     */
    public IAbility[] abilities = new IAbility[] {};

    /**
     * Morph's action
     */
    public IAction action;

    /**
     * Morph's attack
     */
    public IAttackAbility attack;

    /* Properties */

    /**
     * Morph's health 
     */
    public int health = 20;

    /**
     * Morph's speed 
     */
    public float speed = 0.1F;

    /**
     * Whether this morph is "hostile" (meaning that morphed player with hostile 
     * property won't be targeted by other hostile entities). 
     */
    public boolean hostile = false;

    /* Meta information */

    /**
     * Morph's name
     */
    public String name = "";

    /**
     * Morph's category
     */
    public String category = "";

    /* Rendering */

    /**
     * Client morph renderer. It's for {@link EntityPlayer} only, don't try 
     * using it with other types of entities.
     */
    @SideOnly(Side.CLIENT)
    public Render<? extends Entity> renderer;

    /**
     * Render this morph on 2D screen (used in GUIs)
     */
    @SideOnly(Side.CLIENT)
    public abstract void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha);

    /* Update loop */

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    public void update(EntityLivingBase target, IMorphing cap)
    {
        this.setMaxHealth(target, this.health);

        if (speed != 0.1F)
        {
            target.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.speed);
        }

        for (IAbility ability : abilities)
        {
            ability.update(target);
        }
    }

    /* Morph and demorph handlers */

    /**
     * Morph into the current morph
     * 
     * This method responsible for setting up the health of the player to 
     * morph's health and invoke ability's onMorph methods.
     */
    public void morph(EntityLivingBase target)
    {
        this.setHealth(target, this.health);

        /* Pop! */
        target.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

        int per = (int) (target.width * 12);
        int total = per * (int) Math.ceil(target.height);

        for (int i = 0; i < total; i++)
        {
            double angle = ((double) i / per) * Math.PI * 2;

            double x = target.posX + Math.cos(angle) * target.width;
            double y = target.posY + i / per;
            double z = target.posZ + Math.sin(angle) * target.width;

            target.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, target.motionX, target.motionY, target.motionZ);
        }

        for (IAbility ability : this.abilities)
        {
            ability.onMorph(target);
        }
    }

    /**
     * Demorph from the current morph
     * 
     * This method responsible for setting up the health back to player's 
     * default health and invoke ability's onDemorph methods.
     */
    public void demorph(EntityLivingBase target)
    {
        /* 20 is default player's health */
        this.setHealth(target, 20);

        for (IAbility ability : this.abilities)
        {
            ability.onDemorph(target);
        }
    }

    /* Adjusting size */

    /**
     * Update player's size based on given width and height.
     * 
     * This method is responsible for doing trickshots, 360 noscopes while being 
     * morped in a morph. Probably...
     */
    protected void updateSize(EntityLivingBase target, float width, float height)
    {
        if (target instanceof EntityPlayer)
        {
            ((EntityPlayer) target).eyeHeight = height * 0.9F;
        }

        /* This is a total rip-off of EntityPlayer#setSize method */
        if (width != target.width || height != target.height)
        {
            float f = target.width;
            AxisAlignedBB axisalignedbb = target.getEntityBoundingBox();

            target.width = width;
            target.height = height;
            target.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + width, axisalignedbb.minY + height, axisalignedbb.minZ + width));

            if (target.width > f && !target.worldObj.isRemote)
            {
                target.moveEntity(f - target.width, 0.0D, f - target.width);
            }
        }
    }

    /* Adjusting health */

    /**
     * Set player's health proprotional to the current health with given max 
     * health. 
     */
    protected void setHealth(EntityLivingBase target, int health)
    {
        float ratio = target.getHealth() / target.getMaxHealth();
        float proportionalHealth = Math.round(health * ratio);

        this.setMaxHealth(target, health);
        target.setHealth(proportionalHealth <= 0 ? 1 : proportionalHealth);
    }

    /**
     * Set player's max health
     */
    protected void setMaxHealth(EntityLivingBase target, int health)
    {
        if (target.getMaxHealth() != health)
        {
            target.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        }
    }

    /* Safe shortcuts for activating action and attack */

    /**
     * Execute action with (or on) given player 
     */
    public void action(EntityPlayer player)
    {
        if (action != null)
        {
            action.execute(player);
        }
    }

    /**
     * Attack a target 
     */
    public void attack(Entity target, EntityPlayer player)
    {
        if (attack != null)
        {
            attack.attack(target, player);
        }
    }

    /**
     * Clone a morph
     */
    public abstract AbstractMorph clone();

    /**
     * Check either if given object is the same as this morph 
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractMorph)
        {
            AbstractMorph morph = (AbstractMorph) obj;

            return morph.name.equals(this.name);
        }

        return super.equals(obj);
    }

    /* Reading / writing to NBT */

    /**
     * Save abstract morph's properties to NBT compound 
     */
    public void toNBT(NBTTagCompound tag)
    {
        tag.setString("Name", this.name);
        tag.setString("Category", this.category);
    }

    /**
     * Read abstract morph's properties from NBT compound 
     */
    public void fromNBT(NBTTagCompound tag)
    {
        this.name = tag.getString("Name");
        this.category = tag.getString("Category");
    }
}