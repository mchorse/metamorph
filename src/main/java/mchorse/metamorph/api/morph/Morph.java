package mchorse.metamorph.api.morph;

import mchorse.metamorph.api.IAbility;
import mchorse.metamorph.api.IAction;
import mchorse.metamorph.api.IAttackAbility;
import mchorse.metamorph.api.Model;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Morph class
 * 
 * This class is simply responsible for storing morph-related data like its 
 * abilities, action and the model in which it morphs into.
 */
public class Morph
{
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

    /**
     * Morph's model
     */
    public Model model;

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
    public boolean hostile;

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    public void update(EntityPlayer player, IMorphing cap)
    {
        this.updateSize(player, cap);
        this.setMaxHealth(player, this.health);

        if (speed != 0.1F)
        {
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.speed);
        }

        for (IAbility ability : abilities)
        {
            ability.update(player);
        }
    }

    /**
     * Update size of the player based on the given morph.
     */
    public void updateSize(EntityPlayer player, IMorphing cap)
    {
        Model data = cap.getCurrentMorph().model;
        String key = player.isElytraFlying() ? "flying" : (player.isSneaking() ? "sneaking" : "standing");

        float[] pose = data.poses.get(key).size;
        float width = pose[0];
        float height = pose[1];

        player.eyeHeight = height * 0.9F;

        /* This is a total rip-off of EntityPlayer#setSize method */
        if (width != player.width || height != player.height)
        {
            float f = player.width;
            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();

            player.width = width;
            player.height = height;
            player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + width, axisalignedbb.minY + height, axisalignedbb.minZ + width));

            if (player.width > f && !player.worldObj.isRemote)
            {
                player.moveEntity(f - player.width, 0.0D, f - player.width);
            }
        }
    }

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
     * Morph into the current morph
     * 
     * This method responsible for setting up the health of the player to 
     * morph's health and invoke ability's onMorph methods.
     */
    public void morph(EntityPlayer player)
    {
        this.setHealth(player, this.health);
        player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

        for (IAbility ability : this.abilities)
        {
            ability.onMorph(player);
        }
    }

    /**
     * Demorph from the current morph
     * 
     * This method responsible for setting up the health back to player's 
     * default health and invoke ability's onDemorph methods.
     */
    public void demorph(EntityPlayer player)
    {
        /* 20 is default player's health */
        this.setHealth(player, 20);

        for (IAbility ability : this.abilities)
        {
            ability.onDemorph(player);
        }
    }

    /**
     * Set player's health proprotional to the current health with given max 
     * health. 
     */
    private void setHealth(EntityPlayer player, int health)
    {
        float ratio = player.getHealth() / player.getMaxHealth();
        float proportionalHealth = Math.round(health * ratio);

        this.setMaxHealth(player, health);
        player.setHealth(proportionalHealth <= 0 ? 1 : proportionalHealth);
    }

    /**
     * Set player's max health
     */
    private void setMaxHealth(EntityPlayer player, int health)
    {
        if (player.getMaxHealth() != health)
        {
            player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        }
    }
}