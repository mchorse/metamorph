package mchorse.metamorph.api.morph;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.IAbility;
import mchorse.metamorph.api.IAction;
import mchorse.metamorph.api.IAttackAbility;
import mchorse.metamorph.api.abilities.Climb;
import mchorse.metamorph.api.abilities.FireProof;
import mchorse.metamorph.api.abilities.Fly;
import mchorse.metamorph.api.abilities.Glide;
import mchorse.metamorph.api.abilities.Jumping;
import mchorse.metamorph.api.abilities.SnowWalk;
import mchorse.metamorph.api.abilities.SunAllergy;
import mchorse.metamorph.api.abilities.Swim;
import mchorse.metamorph.api.abilities.WaterAllergy;
import mchorse.metamorph.api.abilities.WaterBreath;
import mchorse.metamorph.api.actions.Explode;
import mchorse.metamorph.api.actions.Fireball;
import mchorse.metamorph.api.actions.Jump;
import mchorse.metamorph.api.actions.Snowball;
import mchorse.metamorph.api.attacks.KnockbackAttack;
import mchorse.metamorph.api.attacks.PoisonAttack;
import mchorse.metamorph.api.attacks.WitherAttack;
import net.minecraft.entity.EntityList;

/**
 * Morph manager class
 * 
 * This manager is responsible for managing available morphings.
 */
public class MorphManager
{
    /**
     * Default <s>football</s> morph manager 
     */
    public static final MorphManager INSTANCE = new MorphManager();

    public Map<String, IAbility> abilities = new HashMap<String, IAbility>();
    public Map<String, IAction> actions = new HashMap<String, IAction>();
    public Map<String, IAttackAbility> attacks = new HashMap<String, IAttackAbility>();
    public Map<String, Morph> morphs = new HashMap<String, Morph>();

    private MorphManager()
    {}

    /**
     * Registers default abilities, attacks and actions to manager's maps. 
     * 
     * Other people may register their own morphs and abilities via maps. Don't 
     * override default ones, unless you're extending them. 
     */
    public void register()
    {
        /* Register default abilities */
        abilities.put("climb", new Climb());
        abilities.put("fire_proof", new FireProof());
        abilities.put("fly", new Fly());
        abilities.put("glide", new Glide());
        abilities.put("jumping", new Jumping());
        abilities.put("sun_allergy", new SunAllergy());
        abilities.put("snow_walk", new SnowWalk());
        abilities.put("swim", new Swim());
        abilities.put("water_allergy", new WaterAllergy());
        abilities.put("water_breath", new WaterBreath());

        /* Register default actions */
        actions.put("explode", new Explode());
        actions.put("fireball", new Fireball());
        actions.put("jump", new Jump());
        actions.put("snowball", new Snowball());

        /* Register default attacks */
        attacks.put("poison", new PoisonAttack());
        attacks.put("wither", new WitherAttack());
        attacks.put("knockback", new KnockbackAttack());

        /* Register default morphs */
        this.loadMorphsFromJSON();

        /* Register other morphs */
        this.loadMorphsFromEntityList();
    }

    /**
     * Loads morphs from JSON 
     */
    @SuppressWarnings("serial")
    private void loadMorphsFromJSON()
    {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Morph.class, new MorphAdapter());
        Gson gson = builder.create();

        ClassLoader loader = this.getClass().getClassLoader();
        InputStream stream = loader.getResourceAsStream("assets/metamorph/morphs.json");
        Scanner scanner = new Scanner(stream, "UTF-8");

        Type type = new TypeToken<Map<String, Morph>>()
        {}.getType();
        Map<String, Morph> morphs = gson.fromJson(scanner.useDelimiter("\\A").next(), type);

        scanner.close();

        for (Map.Entry<String, Morph> entry : morphs.entrySet())
        {
            String key = entry.getKey();
            Morph morph = entry.getValue();

            if (!Metamorph.proxy.models.models.containsKey(key))
            {
                Metamorph.log("[WARN]: Model for custom morph '" + key + "' couldn't be found!");

                continue;
            }
            else
            {
                morph.model = Metamorph.proxy.models.models.get(entry.getKey());
            }

            this.morphs.put(key, morph);
        }
    }

    /**
     * Load morphs from {@link EntityList}.
     * 
     * These morphs are quite less awesome than the morphs that were loaded by 
     * the method above, since they're lack abilities, attacks and/or actions. 
     * 
     * Not yet, in the next version.
     */
    private void loadMorphsFromEntityList()
    {}

    /**
     * Get key of the given morph. If given morph isn't registered in morph 
     * manager, it will return empty string.
     */
    public String fromMorph(Morph morph)
    {
        for (Map.Entry<String, Morph> entry : this.morphs.entrySet())
        {
            if (entry.getValue().equals(morph))
            {
                return entry.getKey();
            }
        }

        return "";
    }
}