package mchorse.vanilla_pack;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.client.Minecraft;
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
    public void getMorphs(MorphList morphs)
    {
        for (String name : EntityList.getEntityNameList())
        {
            if (this.hasMorph(name) && !morphs.hasMorph(name))
            {
                this.addMorph(morphs, name, null);
            }
        }

        /* Adding baby animal variants */
        this.addMorph(morphs, "Pig", "{Age:-1}");
        this.addMorph(morphs, "Chicken", "{Age:-1}");
        this.addMorph(morphs, "Cow", "{Age:-1}");
        this.addMorph(morphs, "MushroomCow", "{Age:-1}");
        this.addMorph(morphs, "PolarBear", "{Age:-1}");

        /* Sheep variants */
        this.addMorph(morphs, "Sheep", "{Sheared:1b}");
        this.addMorph(morphs, "Sheep", "{Age:-1}");
        this.addMorph(morphs, "Sheep", "{Age:-1,Sheared:1b}");

        for (int i = 1; i < 16; i++)
        {
            this.addMorph(morphs, "Sheep", "{Color:" + i + "}");
        }

        this.addMorph(morphs, "Sheep", "{CustomName:\"jeb_\"}");
        this.addMorph(morphs, "Sheep", "{Age:-1,CustomName:\"jeb_\"}");

        /* Slime and magma cube variants */
        this.addMorph(morphs, "Slime", "{Size:1}");
        this.addMorph(morphs, "Slime", "{Size:2}");

        this.addMorph(morphs, "LavaSlime", "{Size:1}");
        this.addMorph(morphs, "LavaSlime", "{Size:2}");

        /* Adding cat variants */
        this.addMorph(morphs, "Ozelot", "{Age:-1}");

        for (int i = 1; i < 4; i++)
        {
            this.addMorph(morphs, "Ozelot", "{CatType:" + i + "}");
            this.addMorph(morphs, "Ozelot", "{CatType:" + i + ",Age:-1}");
        }

        /* Adding horse variants */
        this.addMorph(morphs, "EntityHorse", "{Type:0,Variant:1}");
        this.addMorph(morphs, "EntityHorse", "{Type:0,Variant:2}");
        this.addMorph(morphs, "EntityHorse", "{Type:0,Variant:3}");
        this.addMorph(morphs, "EntityHorse", "{Type:0,Variant:4}");
        this.addMorph(morphs, "EntityHorse", "{Type:0,Variant:5}");
        this.addMorph(morphs, "EntityHorse", "{Type:0,Variant:6}");
        this.addMorph(morphs, "EntityHorse", "{Type:1,Variant:0}");
        this.addMorph(morphs, "EntityHorse", "{Type:2,Variant:0}");
        this.addMorph(morphs, "EntityHorse", "{Type:3,Variant:0}");
        this.addMorph(morphs, "EntityHorse", "{Type:4,Variant:0}");

        /* Adding villager variants */
        this.addMorph(morphs, "Villager", "{ProfessionName:\"minecraft:librarian\"}");
        this.addMorph(morphs, "Villager", "{ProfessionName:\"minecraft:priest\"}");
        this.addMorph(morphs, "Villager", "{ProfessionName:\"minecraft:smith\"}");
        this.addMorph(morphs, "Villager", "{ProfessionName:\"minecraft:butcher\"}");

        /* Adding normal bat */
        this.addMorph(morphs, "Bat", "{BatFlags:2}");

        /* Skeleton variants */
        this.addMorph(morphs, "Skeleton", "{SkeletonType:1}");
        this.addMorph(morphs, "Skeleton", "{SkeletonType:2}");

        /* Adding Zombie variants */
        this.addMorph(morphs, "Zombie", "{IsBaby:1b}");

        for (int i = 1; i < 7; i++)
        {
            this.addMorph(morphs, "Zombie", "{ZombieType:" + i + "}");
        }

        /* Adding elder guardian */
        this.addMorph(morphs, "Guardian", "{Elder:1b}");
    }

    /**
     * Add an entity morph to the morph list
     */
    private void addMorph(MorphList morphs, String name, String json)
    {
        World world = Minecraft.getMinecraft().theWorld;
        EntityMorph morph = new EntityMorph();
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

        EntityUtils.stripEntityNBT(data);
        morph.setEntityData(data);
        morphs.addMorphVariant(name, morph);

        /* Setting up a category */
        int index = name.indexOf(".");

        if (index >= 0)
        {
            /* Category for third party mod mobs */
            morph.category = name.substring(0, index);
        }
        else if (entity instanceof EntityAnimal)
        {
            morph.category = "animal";
        }
        else if (entity instanceof EntityMob)
        {
            morph.category = "hostile";
        }
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