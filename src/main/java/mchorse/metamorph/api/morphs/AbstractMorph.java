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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for all different types of morphs
 * 
 * This is an abstract morph. It contains all needed properties for a basic 
 * morph such as abilities, action, attack, health, speed and hotstyle flag.
 * 
 * This class is also responsible for rendering operations. Oh boy, this class 
 * is so huge. I'll have to decompose this thing onto rendering and logic code.
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

    /**
     * Render hands, some morphs might not use this property
     */
    public boolean hands = false;

    /* Meta information */

    /**
     * Morph's name
     */
    public String name = "";

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

    /**
     * Render the entity (in the world) 
     */
    @SideOnly(Side.CLIENT)
    public abstract void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks);

    /**
     * Render the arm for given hand 
     */
    @SideOnly(Side.CLIENT)
    public boolean renderHand(EntityPlayer player, EnumHand hand)
    {
        return false;
    }

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
            AxisAlignedBB aabb = target.getEntityBoundingBox();

            target.width = width;
            target.height = height;
            target.setEntityBoundingBox(new AxisAlignedBB(target.posX - width / 2, aabb.minY, target.posZ - width / 2, target.posX + width / 2, aabb.minY + height, target.posZ + width / 2));
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
    public void action(EntityLivingBase target)
    {
        if (action != null)
        {
            action.execute(target);
        }
    }

    /**
     * Attack a target 
     */
    public void attack(Entity target, EntityLivingBase source)
    {
        if (attack != null)
        {
            attack.attack(target, source);
        }
    }

    /**
     * <p>Clone a morph.</p>
     * 
     * <p>
     * <b>IMPORTANT</b>: when you subclass other morphs, don't forget to override 
     * their method with your own, because otherwise its going to create 
     * another {@link CustomMorph} instance, for example, instead of 
     * MyCustomMorph instance.
     * </p>
     */
    public abstract AbstractMorph clone();

    /**
     * Get width of this morph 
     */
    public abstract float getWidth(EntityLivingBase target);

    /**
     * Get height of this morph 
     */
    public abstract float getHeight(EntityLivingBase target);

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
    }

    /**
     * Read abstract morph's properties from NBT compound 
     */
    public void fromNBT(NBTTagCompound tag)
    {
        this.name = tag.getString("Name");
    }
}