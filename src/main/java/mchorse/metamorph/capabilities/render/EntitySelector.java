package mchorse.metamorph.capabilities.render;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class EntitySelector
{
    public boolean enabled = true;
    public String name = "";
    public String type = "";
    public NBTTagCompound match;
    public NBTTagCompound morph;

    public long time = System.currentTimeMillis();

    public void updateTime()
    {
        this.time = System.currentTimeMillis();
    }

    public boolean matches(EntityLivingBase target)
    {
        if (target == null || !this.enabled)
        {
            return false;
        }

        String nameTag = target.getName();
        ResourceLocation entityName = EntityList.getKey(target);

        if (entityName == null)
        {
            entityName = new ResourceLocation(target instanceof EntityPlayer ? "player" : "");
        }

        String n = this.name;
        String t = this.type;

        boolean negativeName = n.startsWith("!");
        boolean negativeType = t.startsWith("!");

        if (negativeName) n = n.substring(1);
        if (negativeType) t = t.substring(1);

        ResourceLocation rt = new ResourceLocation(t);

        boolean matchesName = this.name.isEmpty() || negativeName != nameTag.equals(n);
        boolean matchesType = this.type.equals("*") || negativeType != entityName.equals(rt);

        if (matchesName && matchesType)
        {
            if (this.match != null && !this.match.hasNoTags())
            {
                return this.match(target.writeToNBT(new NBTTagCompound()));
            }

            return true;
        }

        return false;
    }

    private boolean match(NBTTagCompound entityTag)
    {
        for (String key : this.match.getKeySet())
        {
            NBTBase value = this.match.getTag(key);
            NBTBase entityValue = entityTag.getTag(key);

            if (!value.equals(entityValue))
            {
                return false;
            }
        }

        return true;
    }
}