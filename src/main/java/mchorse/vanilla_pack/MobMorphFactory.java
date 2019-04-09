package mchorse.vanilla_pack;

import java.util.List;

import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.vanilla_pack.morphs.BlockMorph;
import mchorse.vanilla_pack.morphs.IronGolemMorph;
import mchorse.vanilla_pack.morphs.ShulkerMorph;
import mchorse.vanilla_pack.morphs.UndeadMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGiantZombie;
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMorphEditors(List<GuiAbstractMorph> editors)
    {
        editors.add(new GuiAbstractMorph(Minecraft.getMinecraft()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morphName)
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

        for (int i = 1; i <= 4; i++)
        {
            this.addMorph(morphs, world, "minecraft:parrot", "{Variant:" + i + "}");
        }

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
        this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:librarian\"}");
        this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:priest\"}");
        this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:smith\"}");
        this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:butcher\"}");
        this.addMorph(morphs, world, "minecraft:zombie_villager", "{ProfessionName:\"minecraft:nitwit\"}");
        
        /* Adding normal bat */
        this.addMorph(morphs, world, "minecraft:bat", "{BatFlags:2}");

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

        /* Blocks */
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:stone\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:cobblestone\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:grass\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:dirt\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:log\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:diamond_block\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:sponge\"}");
        this.addBlockMorph(morphs, world, "{Block:\"minecraft:deadbush\"}");
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
        try
        {
            EntityMorph morph = this.morphFromName(name);
            EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByIDFromName(new ResourceLocation(name), world);

            if (entity == null)
            {
                System.out.println("Couldn't add morph " + name + ", because it's null!");
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
            String category = "";

            /* Category for third-party mod mobs */
            if (!name.startsWith("minecraft:"))
            {
                category = name.substring(0, name.indexOf(":"));
            }
            else if (entity instanceof EntityDragon || entity instanceof EntityWither || entity instanceof EntityGiantZombie)
            {
                category = "boss";
            }
            else if (entity instanceof EntityAnimal || name.equals("minecraft:bat") || name.equals("minecraft:squid"))
            {
                category = "animal";
            }
            else if (entity instanceof EntityMob || name.equals("minecraft:ghast") || name.equals("minecraft:magma_cube") || name.equals("minecraft:slime") || name.equals("minecraft:shulker"))
            {
                category = "hostile";
            }

            EntityUtils.stripEntityNBT(data);
            morph.setEntityData(data);
            morphs.addMorphVariant(name, category, variant, morph);
        }
        catch (Exception e)
        {
            System.out.println("An error occured during insertion of " + name + " morph!");
            e.printStackTrace();
        }
    }

    /**
     * Add an entity morph to the morph list
     */
    private void addBlockMorph(MorphList morphs, World world, String json)
    {
        try
        {
            BlockMorph morph = new BlockMorph();
            NBTTagCompound tag = JsonToNBT.getTagFromJson(json);

            tag.setString("Name", morph.name);
            morph.fromNBT(tag);

            morphs.addMorphVariant("block", "blocks", morph.block.getBlock().getLocalizedName(), morph);
        }
        catch (Exception e)
        {
            System.out.println("Failed to create a block morph with the data! " + json);
            e.printStackTrace();
        }
    }

    /**
     * Checks if the {@link EntityList} has an entity with given name does 
     * exist and the entity is a living base.
     */
    @Override
    public boolean hasMorph(String name)
    {
        if (name.equals("metamorph.Block"))
        {
            return true;
        }

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

        /* Compatibility for 1.10.2 morph names */
        if (MorphManager.NAME_TO_RL.containsKey(name))
        {
            name = MorphManager.NAME_TO_RL.get(name).toString();
            tag.setString("Name", name);
        }

        if (name.equals("metamorph.Block"))
        {
            BlockMorph morph = new BlockMorph();

            morph.fromNBT(tag);

            return morph;
        }

        if (this.hasMorph(name))
        {
            EntityMorph morph = morphFromName(name);

            morph.fromNBT(tag);

            return morph;
        }

        return null;
    }

    /**
     * Get a morph from a name 
     */
    public EntityMorph morphFromName(String name)
    {
        if (name.equals("minecraft:zombie") || name.equals("minecraft:skeleton") || name.equals("minecraft:zombie_villager"))
        {
            return new UndeadMorph();
        }
        else if (name.equals("minecraft:villager_golem"))
        {
            return new IronGolemMorph();
        }
        else if (name.equals("minecraft:shulker"))
        {
            return new ShulkerMorph();
        }

        return new EntityMorph();
    }
}