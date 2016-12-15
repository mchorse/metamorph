package mchorse.metamorph.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 * Entity utilities methods
 * 
 * Methods that are related to entities are going here.
 */
public class EntityUtils
{
    public static NBTTagCompound stripEntityNBT(NBTTagCompound tag)
    {
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

        if (tag.hasKey("Attributes"))
        {
            List<String> removeAttributes = Lists.newArrayList("generic.followRange");
            NBTTagList attributes = tag.getTagList("Attributes", 10);

            for (int i = attributes.tagCount() - 1; i >= 0; i--)
            {
                if (removeAttributes.contains(attributes.getCompoundTagAt(i).getString("Name")))
                {
                    attributes.removeTag(i);
                }
            }
        }

        return tag;
    }

    /**
     * Compare two {@link NBTTagCompound}s for morphing acquiring 
     */
    public static boolean compareData(NBTTagCompound a, NBTTagCompound b)
    {
        for (String key : a.getKeySet())
        {
            NBTBase aTag = a.getTag(key);
            NBTBase bTag = b.getTag(key);

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
}