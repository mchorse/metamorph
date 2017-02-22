package mchorse.vanilla_pack;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.vanilla_pack.morphs.IronGolemMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
     * What should I implement here?
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morph)
    {
        return null;
    }

    /**
     * Get all available variation of vanilla mobs and default types of custom 
     * mobs
     */
    @Override
    public void getMorphs(MorphList morphs, World world)
    {
        for (ResourceLocation rl : EntityList.getEntityNameList())
        {
            String name = rl.toString();

            if (this.hasMorph(name) && !morphs.hasMorph(name))
            {
                this.addMorph(morphs, world, name, null);
            }
        }

        /* Adding baby animal variants */
        this.addMorph(morphs, world, "minecraft:pig", "{Age:-1}");
        this.addMorph(morphs, world, "minecraft:chicken", "{Age:-1}");
        this.addMorph(morphs, world, "minecraft:cow", "{Age:-1}");
        this.addMorph(morphs, world, "minecraft:mooshroom", "{Age:-1}");
        this.addMorph(morphs, world, "minecraft:polar_bear", "{Age:-1}");

        /* Sheep variants */
        this.addMorph(morphs, world, "minecraft:sheep", "{Sheared:1b}");
        this.addMorph(morphs, world, "minecraft:sheep", "{Age:-1}");
        this.addMorph(morphs, world, "minecraft:sheep", "{Age:-1,Sheared:1b}");

        for (int i = 1; i < 16; i++)
        {
            this.addMorph(morphs, world, "minecraft:sheep", "{Color:" + i + "}");
        }

        this.addMorph(morphs, world, "minecraft:sheep", "Jeb", "{CustomName:\"jeb_\"}");
        this.addMorph(morphs, world, "minecraft:sheep", "Baby Jeb", "{Age:-1,CustomName:\"jeb_\"}");

        /* Slime and magma cube variants */
        this.addMorph(morphs, world, "minecraft:slime", "{Size:1}");
        this.addMorph(morphs, world, "minecraft:slime", "{Size:2}");

        this.addMorph(morphs, world, "minecraft:magma_cube", "{Size:1}");
        this.addMorph(morphs, world, "minecraft:magma_cube", "{Size:2}");

        /* Adding cat variants */
        this.addMorph(morphs, world, "minecraft:ocelot", "{Age:-1}");

        for (int i = 1; i < 4; i++)
        {
            this.addMorph(morphs, world, "minecraft:ocelot", "{CatType:" + i + "}");
            this.addMorph(morphs, world, "minecraft:ocelot", "{CatType:" + i + ",Age:-1}");
        }

        /* Adding horse variants */
        this.addMorph(morphs, world, "minecraft:horse", "{Variant:1}");
        this.addMorph(morphs, world, "minecraft:horse", "{Variant:2}");
        this.addMorph(morphs, world, "minecraft:horse", "{Variant:3}");
        this.addMorph(morphs, world, "minecraft:horse", "{Variant:4}");
        this.addMorph(morphs, world, "minecraft:horse", "{Variant:5}");
        this.addMorph(morphs, world, "minecraft:horse", "{Variant:6}");

        /* Adding villager variants */
        this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:librarian\"}");
        this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:priest\"}");
        this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:smith\"}");
        this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:butcher\"}");
        this.addMorph(morphs, world, "minecraft:villager", "{ProfessionName:\"minecraft:nitwit\"}");

        /* Adding zombie villagers */
        for (int i = 1; i < 6; i++)
        {
            this.addMorph(morphs, world, "minecraft:zombie_villager", "{Profession:" + i + "}");
        }

        /* Adding normal bat */
        this.addMorph(morphs, world, "minecraft:bar", "{BatFlags:2}");

        /* Adding Zombie variants */
        this.addMorph(morphs, world, "minecraft:zombie", "Baby", "{IsBaby:1b}");

        /* Adding llama variants */
        for (int i = 1; i < 4; i++)
        {
            this.addMorph(morphs, world, "minecraft:llama", "{Variant:" + i + "}");
        }

        /* Adding rabbit variants */
        for (int i = 1; i < 6; i++)
        {
            this.addMorph(morphs, world, "minecraft:rabbit", "{RabbitType:" + i + "}");
        }

        this.addMorph(morphs, world, "minecraft:rabbit", "Toast", "{CustomName:\"Toast\"}");
    }

    /**
     * Add an entity morph to the morph list
     */
    private void addMorph(MorphList morphs, World world, String name, String json)
    {
        this.addMorph(morphs, world, name, "", json);
    }

    /**
     * Add an entity morph to the morph list
     */
    private void addMorph(MorphList morphs, World world, String name, String variant, String json)
    {
        EntityMorph morph = name.equals("minecraft:villager_golem") ? new IronGolemMorph() : new EntityMorph();
        EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByIDFromName(new ResourceLocation(name), world);

        if (entity == null)
        {
            System.out.println("Couldn't add morph " + name + "!");
            return;
        }

        NBTTagCompound data = entity.serializeNBT();

        morph.name = name;

        if (json != null)
        {
            try
            {
                data.merge(JsonToNBT.getTagFromJson(json));
            }
            catch (NBTException e)
            {
                System.out.println("Failed to merge provided JSON data for '" + name + "' morph!");
                e.printStackTrace();
            }
        }

        /* Setting up a category */
        int index = name.indexOf(".");
        String category = "";

        /* Category for third-party mod mobs */
        if (index >= 0)
        {
            category = name.substring(0, index);
        }
        else if (entity instanceof EntityAnimal)
        {
            category = "animal";
        }
        else if (entity instanceof EntityMob)
        {
            category = "hostile";
        }

        EntityUtils.stripEntityNBT(data);
        morph.setEntityData(data);
        morphs.addMorphVariant(name, category, variant, morph);
    }

    /**
     * Checks if the {@link EntityList} has an entity with given name does 
     * exist and the entity is a living base.
     */
    @Override
    public boolean hasMorph(String name)
    {
        Class<? extends Entity> clazz = null;
        ResourceLocation key = new ResourceLocation(name);

        for (EntityEntry entity : ForgeRegistries.ENTITIES)
        {
            if (entity.getRegistryName().equals(key))
            {
                clazz = entity.getEntityClass();
            }
        }

        return clazz == null ? false : EntityLivingBase.class.isAssignableFrom(clazz);
    }

    /**
     * Create an {@link EntityMorph} from NBT
     */
    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        String name = tag.getString("Name");

        if (MorphManager.NAME_TO_RL.containsKey(name))
        {
            name = MorphManager.NAME_TO_RL.get(name).toString();
        }

        if (this.hasMorph(name))
        {
            EntityMorph morph = name.equals("minecraft:villager_golem") ? new IronGolemMorph() : new EntityMorph();

            morph.fromNBT(tag);

            return morph;
        }

        return null;
    }
}