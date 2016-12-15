package mchorse.vanilla_pack;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
    /**
     * Nothing to register here, since all of the morphs are generated on 
     * runtime 
     */
    @Override
    public void register(MorphManager manager)
    {}

    /**
     * What should I write here?
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    /**
     * Get all available variation of vanilla mobs and default types of custom 
     * mobs
     */
    @Override
    public List<AbstractMorph> getMorphs()
    {
        List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

        for (String name : EntityList.getEntityNameList())
        {
            if (this.hasMorph(name))
            {
                World world = Minecraft.getMinecraft().theWorld;
                EntityMorph morph = new EntityMorph();
                EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByName(name, world);

                morph.name = name;
                morph.setEntity(entity);
                morphs.add(morph);
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
        /* Nope! */
        if (name.equals("metamorph.Morph"))
        {
            return false;
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