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
import mchorse.metamorph.api.abilities.SunAllergy;
import mchorse.metamorph.api.abilities.Swim;
import mchorse.metamorph.api.abilities.WaterAllergy;
import mchorse.metamorph.api.abilities.WaterBreath;
import mchorse.metamorph.api.actions.Explode;
import mchorse.metamorph.api.actions.Fireball;
import mchorse.metamorph.api.actions.Jump;
import mchorse.metamorph.api.attacks.KnockbackAttack;
import mchorse.metamorph.api.attacks.WitherAttack;

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
     * Registers default abilities and actions to manager's maps. 
     * 
     * Other people may register their own morphs and abilities via maps. Don't 
     * override default ones, unless you're extending them. 
     */
    public void register()
    {
        /* Register abilities */
        abilities.put("climb", new Climb());
        abilities.put("fire_proof", new FireProof());
        abilities.put("fly", new Fly());
        abilities.put("glide", new Glide());
        abilities.put("sun_allergy", new SunAllergy());
        abilities.put("swim", new Swim());
        abilities.put("water_allergy", new WaterAllergy());
        abilities.put("water_breath", new WaterBreath());

        /* Register actions */
        actions.put("explode", new Explode());
        actions.put("fireball", new Fireball());
        actions.put("jump", new Jump());

        /* Register attacks */
        attacks.put("wither", new WitherAttack());
        attacks.put("knockback", new KnockbackAttack());

        /* Register morphs */
        this.loadFromJSON();
    }

    /**
     * Loads morphs from JSON 
     */
    private void loadFromJSON()
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

            if (!Metamorph.proxy.models.models.containsKey(key))
            {
                System.out.println("[WARN]: '" + key + "' morph couldn't be loaded");

                continue;
            }
            else
            {
                Morph morph = entry.getValue();
                morph.model = Metamorph.proxy.models.models.get(entry.getKey());

                this.morphs.put(key, morph);
            }
        }
    }

    /**
     * Get key of the given morph. If given morph isn't registered in this
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