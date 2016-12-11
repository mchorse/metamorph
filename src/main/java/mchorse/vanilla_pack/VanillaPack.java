package mchorse.vanilla_pack;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Scanner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.Model;
import mchorse.metamorph.api.ModelManager;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.json.MorphAdapter;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Vanilla morph pack
 * 
 * This class is responsible for registering all shit related to vanilla morph 
 * pack. That's including abilities, attacks and actions for vanilla morphs.
 */
public class VanillaPack
{
    /**
     * Register abilities, attacks and actions
     */
    public static void register()
    {
        /* Define shortcuts */
        Map<String, IAbility> abilities = MorphManager.INSTANCE.abilities;
        Map<String, IAttackAbility> attacks = MorphManager.INSTANCE.attacks;
        Map<String, IAction> actions = MorphManager.INSTANCE.actions;

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

        registerMorphs();
    }

    /**
     * Register morphs from JSON 
     */
    private static void registerMorphs()
    {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(CustomMorph.class, new MorphAdapter());
        Gson gson = builder.create();

        ClassLoader loader = VanillaPack.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream("assets/metamorph/morphs.json");
        Scanner scanner = new Scanner(stream, "UTF-8");

        @SuppressWarnings("serial")
        Type type = new TypeToken<Map<String, CustomMorph>>()
        {}.getType();

        Map<String, CustomMorph> data = gson.fromJson(scanner.useDelimiter("\\A").next(), type);
        Map<String, CustomMorph> morphs = MorphManager.INSTANCE.morphs;

        scanner.close();

        for (Map.Entry<String, CustomMorph> entry : data.entrySet())
        {
            String key = entry.getKey();
            CustomMorph morph = entry.getValue();

            if (!Metamorph.proxy.models.models.containsKey(key))
            {
                Metamorph.log("Model for custom morph '" + key + "' couldn't be found!");

                continue;
            }
            else
            {
                morph.model = Metamorph.proxy.models.models.get(entry.getKey());
            }

            morphs.put(key, morph);
        }
    }

    /**
     * Load served based custom models 
     */
    public static void loadModels(ModelManager models)
    {
        /* Animals */
        loadModel(models, "Bat");
        loadModel(models, "Chicken");
        loadModel(models, "Cow");
        loadModel(models, "EntityHorse", "horse");
        loadModel(models, "MushroomCow", "mooshroom");
        loadModel(models, "Ozelot", "ocelot");
        loadModel(models, "Pig");
        loadModel(models, "PolarBear", "polar_bear");
        loadModel(models, "Rabbit");
        loadModel(models, "Sheep");
        loadModel(models, "Squid");
        loadModel(models, "Wolf");

        /* Neutral mobs */
        loadModel(models, "Enderman");
        loadModel(models, "PigZombie", "zombie_pigman");
        loadModel(models, "SnowMan", "snow_man");
        loadModel(models, "Villager");
        loadModel(models, "VillagerGolem", "iron_golem");

        /* Hostile mobs */
        loadModel(models, "Blaze");
        loadModel(models, "CaveSpider", "cave_spider");
        loadModel(models, "Creeper");
        loadModel(models, "Ghast");
        loadModel(models, "Guardian");
        loadModel(models, "LavaSlime", "magma_cube");
        loadModel(models, "Silverfish");
        loadModel(models, "Skeleton");
        loadModel(models, "Slime");
        loadModel(models, "Spider");
        loadModel(models, "Witch");
        loadModel(models, "WitherSkeleton", "wither_skeleton");
        loadModel(models, "Zombie");
    }

    /**
     * Load model with name and lowercase'd model name
     */
    private static void loadModel(ModelManager models, String model)
    {
        loadModel(models, model, model.toLowerCase());
    }

    /**
     * Load model with name and filename
     */
    private static void loadModel(ModelManager models, String model, String filename)
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

    /**
     * Load client custom models
     */
    @SideOnly(Side.CLIENT)
    public static void loadClientModels(ModelManager models)
    {
        /* Animals */
        loadClientModel(models, "Bat");
        loadClientModel(models, "Chicken");
        loadClientModel(models, "Cow");
        loadClientModel(models, "EntityHorse");
        loadClientModel(models, "MushroomCow");
        loadClientModel(models, "Ozelot");
        loadClientModel(models, "Pig");
        loadClientModel(models, "PolarBear");
        loadClientModel(models, "Rabbit");
        loadClientModel(models, "Sheep");
        loadClientModel(models, "Squid");
        loadClientModel(models, "Wolf");

        /* Neutral mobs */
        loadClientModel(models, "Enderman");
        loadClientModel(models, "PigZombie");
        loadClientModel(models, "SnowMan");
        loadClientModel(models, "Villager");
        loadClientModel(models, "VillagerGolem");

        /* Hostile mobs */
        loadClientModel(models, "Blaze");
        loadClientModel(models, "CaveSpider");
        loadClientModel(models, "Creeper");
        loadClientModel(models, "Ghast");
        loadClientModel(models, "Guardian");
        loadClientModel(models, "LavaSlime");
        loadClientModel(models, "Silverfish");
        loadClientModel(models, "Skeleton");
        loadClientModel(models, "Slime");
        loadClientModel(models, "Spider");
        loadClientModel(models, "Witch");
        loadClientModel(models, "WitherSkeleton");
        loadClientModel(models, "Zombie");
    }

    /**
     * Load a client model with given name and given data custom model from 
     * models registry 
     */
    private static void loadClientModel(ModelManager models, String name)
    {
        loadClientModel(name, models.models.get(name));
    }

    /**
     * Load a client model for given name with given data custom model
     */
    @SideOnly(Side.CLIENT)
    private static void loadClientModel(String name, Model data)
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