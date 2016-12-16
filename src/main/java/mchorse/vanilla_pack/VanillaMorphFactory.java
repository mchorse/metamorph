package mchorse.vanilla_pack;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.Model;
import mchorse.metamorph.api.ModelManager;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.json.MorphAdapter;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.CustomMorph;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.model.parsing.ModelParser;
import mchorse.vanilla_pack.abilities.BlazeSmoke;
import mchorse.vanilla_pack.abilities.Climb;
import mchorse.vanilla_pack.abilities.Ender;
import mchorse.vanilla_pack.abilities.FireProof;
import mchorse.vanilla_pack.abilities.Fly;
import mchorse.vanilla_pack.abilities.Glide;
import mchorse.vanilla_pack.abilities.Hungerless;
import mchorse.vanilla_pack.abilities.Jumping;
import mchorse.vanilla_pack.abilities.NightVision;
import mchorse.vanilla_pack.abilities.PreventFall;
import mchorse.vanilla_pack.abilities.SnowWalk;
import mchorse.vanilla_pack.abilities.SunAllergy;
import mchorse.vanilla_pack.abilities.Swim;
import mchorse.vanilla_pack.abilities.WaterAllergy;
import mchorse.vanilla_pack.abilities.WaterBreath;
import mchorse.vanilla_pack.actions.Explode;
import mchorse.vanilla_pack.actions.Fireball;
import mchorse.vanilla_pack.actions.Jump;
import mchorse.vanilla_pack.actions.Potions;
import mchorse.vanilla_pack.actions.Snowball;
import mchorse.vanilla_pack.actions.Teleport;
import mchorse.vanilla_pack.attacks.KnockbackAttack;
import mchorse.vanilla_pack.attacks.PoisonAttack;
import mchorse.vanilla_pack.attacks.WitherAttack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Vanilla morph factory 
 * 
 * This morph factory is responsible for registering vanilla-based abilities, 
 * attacks and actions.
 */
public class VanillaMorphFactory implements IMorphFactory
{
    /**
     * Factory'r registered morphs 
     */
    private Map<String, CustomMorph> morphs = new HashMap<String, CustomMorph>();

    /**
     * Register method
     * 
     * This method is responsible for registering abilities, actions, attacks, 
     * models and morphs.
     */
    @Override
    public void register(MorphManager manager)
    {
        /* Define shortcuts */
        Map<String, IAbility> abilities = manager.abilities;
        Map<String, IAttackAbility> attacks = manager.attacks;
        Map<String, IAction> actions = manager.actions;

        /* Register default abilities */
        abilities.put("climb", new Climb());
        abilities.put("ender", new Ender());
        abilities.put("fire_proof", new FireProof());
        abilities.put("fly", new Fly());
        abilities.put("glide", new Glide());
        abilities.put("hungerless", new Hungerless());
        abilities.put("jumping", new Jumping());
        abilities.put("night_vision", new NightVision());
        abilities.put("prevent_fall", new PreventFall());
        abilities.put("smoke", new BlazeSmoke());
        abilities.put("snow_walk", new SnowWalk());
        abilities.put("sun_allergy", new SunAllergy());
        abilities.put("swim", new Swim());
        abilities.put("water_allergy", new WaterAllergy());
        abilities.put("water_breath", new WaterBreath());

        /* Register default actions */
        actions.put("explode", new Explode());
        actions.put("fireball", new Fireball());
        actions.put("jump", new Jump());
        actions.put("potions", new Potions());
        actions.put("snowball", new Snowball());
        actions.put("teleport", new Teleport());

        /* Register default attacks */
        attacks.put("poison", new PoisonAttack());
        attacks.put("wither", new WitherAttack());
        attacks.put("knockback", new KnockbackAttack());

        this.registerModels(manager.models);
        this.registerMorphs(manager.models);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {
        for (AbstractMorph morph : this.morphs.values())
        {
            morph.renderer = ClientProxy.playerRenderer;
        }

        this.registerClientModels(manager.models);
    }

    @Override
    public List<AbstractMorph> getMorphs()
    {
        return new ArrayList<AbstractMorph>(this.morphs.values());
    }

    @Override
    public boolean hasMorph(String name)
    {
        return this.morphs.containsKey(name);
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        CustomMorph morph = this.morphs.get(tag.getString("Name"));

        return morph == null ? null : morph.clone();
    }

    /* Custom Models */

    /**
     * Register morphs from JSON file
     * 
     * This method is responsible for registering all JSON 
     */
    protected void registerMorphs(ModelManager manager)
    {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(CustomMorph.class, new MorphAdapter());
        Gson gson = builder.create();

        ClassLoader loader = this.getClass().getClassLoader();
        InputStream stream = loader.getResourceAsStream("assets/metamorph/morphs.json");
        Scanner scanner = new Scanner(stream, "UTF-8");

        @SuppressWarnings("serial")
        Type type = new TypeToken<Map<String, CustomMorph>>()
        {}.getType();

        Map<String, CustomMorph> data = gson.fromJson(scanner.useDelimiter("\\A").next(), type);

        scanner.close();

        for (Map.Entry<String, CustomMorph> entry : data.entrySet())
        {
            String key = entry.getKey();
            CustomMorph morph = entry.getValue();

            morph.name = key;
            morph.model = manager.models.get(entry.getKey());
            this.morphs.put(key, morph);
        }
    }

    /**
     * Load served based custom models
     */
    private void registerModels(ModelManager models)
    {
        /* Animals */
        this.loadModel(models, "Bat");
        this.loadModel(models, "Chicken");
        this.loadModel(models, "Cow");
        this.loadModel(models, "EntityHorse", "horse");
        this.loadModel(models, "MushroomCow", "mooshroom");
        this.loadModel(models, "Ozelot", "ocelot");
        this.loadModel(models, "Pig");
        this.loadModel(models, "PolarBear", "polar_bear");
        this.loadModel(models, "Rabbit");
        this.loadModel(models, "Sheep");
        this.loadModel(models, "Squid");
        this.loadModel(models, "Wolf");

        /* Neutral mobs */
        this.loadModel(models, "Enderman");
        this.loadModel(models, "PigZombie", "zombie_pigman");
        this.loadModel(models, "SnowMan", "snow_man");
        this.loadModel(models, "Villager");
        this.loadModel(models, "VillagerGolem", "iron_golem");

        /* Hostile mobs */
        this.loadModel(models, "Blaze");
        this.loadModel(models, "CaveSpider", "cave_spider");
        this.loadModel(models, "Creeper");
        this.loadModel(models, "Ghast");
        this.loadModel(models, "Guardian");
        this.loadModel(models, "LavaSlime", "magma_cube");
        this.loadModel(models, "Silverfish");
        this.loadModel(models, "Skeleton");
        this.loadModel(models, "Slime");
        this.loadModel(models, "Spider");
        this.loadModel(models, "Witch");
        this.loadModel(models, "WitherSkeleton", "wither_skeleton");
        this.loadModel(models, "Zombie");
    }

    /**
     * Load model with name and lowercase'd model name
     */
    private void loadModel(ModelManager models, String model)
    {
        loadModel(models, model, model.toLowerCase());
    }

    /**
     * Load model with name and filename
     */
    private void loadModel(ModelManager models, String model, String filename)
    {
        try
        {
            models.load(model, filename);
        }
        catch (Exception e)
        {
            System.out.println("An exception was raised when loading '" + model + "' model!");
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerClientModels(ModelManager models)
    {
        /* Animals */
        this.loadClientModel(models, "Bat");
        this.loadClientModel(models, "Chicken");
        this.loadClientModel(models, "Cow");
        this.loadClientModel(models, "EntityHorse");
        this.loadClientModel(models, "MushroomCow");
        this.loadClientModel(models, "Ozelot");
        this.loadClientModel(models, "Pig");
        this.loadClientModel(models, "PolarBear");
        this.loadClientModel(models, "Rabbit");
        this.loadClientModel(models, "Sheep");
        this.loadClientModel(models, "Squid");
        this.loadClientModel(models, "Wolf");

        /* Neutral mobs */
        this.loadClientModel(models, "Enderman");
        this.loadClientModel(models, "PigZombie");
        this.loadClientModel(models, "SnowMan");
        this.loadClientModel(models, "Villager");
        this.loadClientModel(models, "VillagerGolem");

        /* Hostile mobs */
        this.loadClientModel(models, "Blaze");
        this.loadClientModel(models, "CaveSpider");
        this.loadClientModel(models, "Creeper");
        this.loadClientModel(models, "Ghast");
        this.loadClientModel(models, "Guardian");
        this.loadClientModel(models, "LavaSlime");
        this.loadClientModel(models, "Silverfish");
        this.loadClientModel(models, "Skeleton");
        this.loadClientModel(models, "Slime");
        this.loadClientModel(models, "Spider");
        this.loadClientModel(models, "Witch");
        this.loadClientModel(models, "WitherSkeleton");
        this.loadClientModel(models, "Zombie");
    }

    /**
     * Load a client model with given name and given data custom model from 
     * models registry 
     */
    @SideOnly(Side.CLIENT)
    private void loadClientModel(ModelManager models, String name)
    {
        loadClientModel(name, models.models.get(name));
    }

    /**
     * Load a client model for given name with given data custom model
     */
    @SideOnly(Side.CLIENT)
    private void loadClientModel(String name, Model data)
    {
        if (data == null)
        {
            Metamorph.log("Client custom model by name " + name + " couldn't be loaded!");

            return;
        }

        if (data.model.isEmpty())
        {
            /* Parse default type of model */
            ModelParser.parse(name, data);
        }
        else
        {
            try
            {
                @SuppressWarnings("unchecked")
                Class<? extends ModelCustom> clazz = (Class<? extends ModelCustom>) Class.forName(data.model);

                /* Parse custom custom (overcustomized) model */
                ModelParser.parse(name, data, clazz);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}