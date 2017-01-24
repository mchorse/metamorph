package mchorse.metamorph.api;

import java.util.List;

import com.google.common.collect.Lists;

import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 * Entity utilities methods
 * 
 * Methods that are related to {@link EntityMorph} are going here.
 */
public class EntityUtils
{
    /**
     * List of attributes to remove
     */
    private static List<String> removeAttributes = Lists.newArrayList("generic.followRange");

    /**
     * Strip some common {@link Entity} related tags, so there won't be 
     * interference with comparing two tags on  
     */
    public static NBTTagCompound stripEntityNBT(NBTTagCompound tag)
    {
        /* Custom displayed name */
        if (tag.hasKey("CustomName", 8))
        {
            String name = tag.getString("CustomName");

            if (!name.equals("jeb_") && !name.equals("Grumm") && !name.equals("Dinnerbone") && !name.equals("Toast"))
            {
                tag.removeTag("CustomName");
            }
        }

        /* Meta stuff */
        tag.removeTag("Dimension");
        tag.removeTag("HurtTime");
        tag.removeTag("DeathTime");
        tag.removeTag("HurtByTimestamp");
        tag.removeTag("Health");
        tag.removeTag("PortalCooldown");
        tag.removeTag("Leashed");
        tag.removeTag("Air");
        tag.removeTag("id");
        tag.removeTag("Invulnerable");

        /* Inventory and equipment */
        tag.removeTag("ArmorDropChances");
        tag.removeTag("HandDropChances");
        tag.removeTag("HandItems");
        tag.removeTag("Inventory");
        tag.removeTag("LeftHanded");
        tag.removeTag("CanPickUpLoot");

        /* Space data */
        tag.removeTag("Pos");
        tag.removeTag("Motion");
        tag.removeTag("Rotation");
        tag.removeTag("FallDistance");
        tag.removeTag("FallFlying");
        tag.removeTag("OnGround");
        tag.removeTag("Fire");
        tag.removeTag("ArmorItems");

        /* UUID */
        tag.removeTag("UUIDLeast");
        tag.removeTag("UUIDMost");

        /* Attributes */
        if (tag.hasKey("Attributes"))
        {
            NBTTagList attributes = tag.getTagList("Attributes", 10);

            for (int i = attributes.tagCount() - 1; i >= 0; i--)
            {
                if (removeAttributes.contains(attributes.getCompoundTagAt(i).getString("Name")))
                {
                    attributes.removeTag(i);
                }
            }
        }

        /* Shulker tags stripping */
        tag.removeTag("Peek");
        tag.removeTag("AttachFace");
        tag.removeTag("APX");
        tag.removeTag("APY");
        tag.removeTag("APZ");

        return tag;
    }

    /**
     * Compare two {@link NBTTagCompound}s for morphing acquiring
     */
    public static boolean compareData(NBTTagCompound a, NBTTagCompound b)
    {
        /* Different count of tags? They're different */
        if (a.getSize() != b.getSize())
        {
            return false;
        }

        for (String key : a.getKeySet())
        {
            NBTBase aTag = a.getTag(key);
            NBTBase bTag = b.getTag(key);

            /* Supporting condition for size check above, in case if the size 
             * the same, but different keys are missing */
            if (bTag == null)
            {
                return false;
            }

            /* We check only strings and primitives, lists and compounds aren't 
             * concern of mine */
            if (!(aTag instanceof NBTPrimitive) && !(aTag instanceof NBTTagString))
            {
                continue;
            }

            if (!aTag.equals(bTag))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Get slot for given index of {@link Entity#getEquipmentAndArmor()}. I 
     * assume that it would be the same all the time, across all of the 
     * subclasses of {@link Entity}.
     */
    public static EntityEquipmentSlot slotForIndex(int index)
    {
        EntityEquipmentSlot slot = EntityEquipmentSlot.MAINHAND;

        switch (index)
        {
            case 1:
                slot = EntityEquipmentSlot.OFFHAND;
                break;
            case 2:
                slot = EntityEquipmentSlot.FEET;
                break;
            case 3:
                slot = EntityEquipmentSlot.LEGS;
                break;
            case 4:
                slot = EntityEquipmentSlot.CHEST;
                break;
            case 5:
                slot = EntityEquipmentSlot.HEAD;
                break;
        }

        return slot;
    }

    /**
     * Get morph from an entity 
     */
    public static AbstractMorph getMorph(EntityLivingBase entity)
    {
        if (entity instanceof IMorphProvider)
        {
            return ((IMorphProvider) entity).getMorph();
        }
        else
        {
            IMorphing cap = entity.getCapability(MorphingProvider.MORPHING_CAP, null);

            if (cap != null)
            {
                return cap.getCurrentMorph();
            }
        }

        return null;
    }

    /**
     * Get string pose for entity based on his attributes
     */
    public static String getPose(EntityLivingBase entity)
    {
        if (entity.isElytraFlying())
        {
            return "flying";
        }
        else if (entity.isRiding())
        {
            return "riding";
        }
        else if (entity.isSneaking())
        {
            return "sneaking";
        }

        return "standing";
    }
}