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
     * What should I implement here?
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(String morphName)
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
        for (String name : EntityList.getEntityNameList())
        {
            if (this.hasMorph(name) && !morphs.hasMorph(name))
            {
                this.addMorph(morphs, world, name, null);
            }
        }

        /* Adding baby animal variants */
        this.addMorph(morphs, world, "Pig", "{Age:-1}");
        this.addMorph(morphs, world, "Chicken", "{Age:-1}");
        this.addMorph(morphs, world, "Cow", "{Age:-1}");
        this.addMorph(morphs, world, "MushroomCow", "{Age:-1}");
        this.addMorph(morphs, world, "PolarBear", "{Age:-1}");

        /* Sheep variants */
        this.addMorph(morphs, world, "Sheep", "{Sheared:1b}");
        this.addMorph(morphs, world, "Sheep", "{Age:-1}");
        this.addMorph(morphs, world, "Sheep", "{Age:-1,Sheared:1b}");

        for (int i = 1; i < 16; i++)
        {
            this.addMorph(morphs, world, "Sheep", "{Color:" + i + "}");
        }

        this.addMorph(morphs, world, "Sheep", "Jeb", "{CustomName:\"jeb_\"}");
        this.addMorph(morphs, world, "Sheep", "Baby Jeb", "{Age:-1,CustomName:\"jeb_\"}");

        /* Slime and magma cube variants */
        this.addMorph(morphs, world, "Slime", "{Size:1}");
        this.addMorph(morphs, world, "Slime", "{Size:2}");

        this.addMorph(morphs, world, "LavaSlime", "{Size:1}");
        this.addMorph(morphs, world, "LavaSlime", "{Size:2}");

        /* Adding cat variants */
        this.addMorph(morphs, world, "Ozelot", "{Age:-1}");

        for (int i = 1; i < 4; i++)
        {
            this.addMorph(morphs, world, "Ozelot", "{CatType:" + i + "}");
            this.addMorph(morphs, world, "Ozelot", "{CatType:" + i + ",Age:-1}");
        }

        /* Adding horse variants */
        this.addMorph(morphs, world, "EntityHorse", "{Type:0,Variant:1}");
        this.addMorph(morphs, world, "EntityHorse", "{Type:0,Variant:2}");
        this.addMorph(morphs, world, "EntityHorse", "{Type:0,Variant:3}");
        this.addMorph(morphs, world, "EntityHorse", "{Type:0,Variant:4}");
        this.addMorph(morphs, world, "EntityHorse", "{Type:0,Variant:5}");
        this.addMorph(morphs, world, "EntityHorse", "{Type:0,Variant:6}");
        this.addMorph(morphs, world, "EntityHorse", "Donkey", "{Type:1,Variant:0}");
        this.addMorph(morphs, world, "EntityHorse", "Mule", "{Type:2,Variant:0}");
        this.addMorph(morphs, world, "EntityHorse", "Skeleton", "{Type:3,Variant:0}");
        this.addMorph(morphs, world, "EntityHorse", "Zombie", "{Type:4,Variant:0}");

        /* Adding villager variants */
        this.addMorph(morphs, world, "Villager", "{ProfessionName:\"minecraft:librarian\"}");
        this.addMorph(morphs, world, "Villager", "{ProfessionName:\"minecraft:priest\"}");
        this.addMorph(morphs, world, "Villager", "{ProfessionName:\"minecraft:smith\"}");
        this.addMorph(morphs, world, "Villager", "{ProfessionName:\"minecraft:butcher\"}");

        /* Adding normal bat */
        this.addMorph(morphs, world, "Bat", "{BatFlags:2}");

        /* Skeleton variants */
        this.addMorph(morphs, world, "Skeleton", "Wither", "{SkeletonType:1}");
        this.addMorph(morphs, world, "Skeleton", "Stray", "{SkeletonType:2}");

        /* Adding Zombie variants */
        this.addMorph(morphs, world, "Zombie", "Baby", "{IsBaby:1b}");

        for (int i = 1; i < 7; i++)
        {
            this.addMorph(morphs, world, "Zombie", "{ZombieType:" + i + "}");
        }

        /* Adding elder guardian */
        this.addMorph(morphs, world, "Guardian", "Elder", "{Elder:1b}");

        /* Adding rabbit variants */
        for (int i = 1; i < 6; i++)
        {
            this.addMorph(morphs, world, "Rabbit", "{RabbitType:" + i + "}");
        }

        this.addMorph(morphs, world, "Rabbit", "Toast", "{CustomName:\"Toast\"}");
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
        EntityMorph morph = name.equals("VillagerGolem") ? new IronGolemMorph() : new EntityMorph();
        EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByName(name, world);
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
            EntityMorph morph = name.equals("VillagerGolem") ? new IronGolemMorph() : new EntityMorph();

            morph.fromNBT(tag);

            return morph;
        }

        return null;
    }
}