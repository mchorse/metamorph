package mchorse.vanilla_pack;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Mob morph factory
 * 
 * This is underlying morph factory. It's responsible for generating 
 * {@link EntityMorph} out of 
 */
public class MobMorphFactory implements IMorphFactory
{
    @Override
    public void register(MorphManager manager)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    /**
     * Get available vanilla morphs
     */
    @Override
    public List<AbstractMorph> getMorphs()
    {
        List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

        for (String name : EntityList.getEntityNameList())
        {
            if (this.hasMorph(name))
            {
                // TODO: implement it
            }
        }

        return morphs;
    }

    /**
     * Checks if the {@link EntityList} has an entity with given name does 
     * exist and the entity is a living base.
     */
    @Override
    public boolean hasMorph(String name)
    {
        if (name.equals("metamorph.Morph"))
        {
            // return false;
        }

        Class<? extends Entity> clazz = EntityList.NAME_TO_CLASS.get(name);

        if (clazz != null)
        {
            return EntityLivingBase.class.isAssignableFrom(clazz);
        }

        return false;
    }

    /**
     * Create an {@link EntityMorph} from NBT
     */
    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        String name = tag.getString("Name");

        if (this.hasMorph(name))
        {
            EntityMorph morph = new EntityMorph();

            morph.fromNBT(tag);

            return morph;
        }

        return null;
    }
}