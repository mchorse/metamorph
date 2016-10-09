package mchorse.metamorph.api.morph;

import java.util.HashMap;
import java.util.Map;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.IAbility;
import mchorse.metamorph.api.IAction;
import mchorse.metamorph.api.abilities.Climb;
import mchorse.metamorph.api.abilities.FireProof;
import mchorse.metamorph.api.abilities.Glide;
import mchorse.metamorph.api.abilities.SunAllergy;
import mchorse.metamorph.api.abilities.Swim;
import mchorse.metamorph.api.abilities.WaterAllergy;
import mchorse.metamorph.api.abilities.WaterBreath;

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
        abilities.put("glide", new Glide());
        abilities.put("sun_allergy", new SunAllergy());
        abilities.put("swim", new Swim());
        abilities.put("water_allergy", new WaterAllergy());
        abilities.put("water_breath", new WaterBreath());

        /* Register actions */

        /* Register morphs */
        this.loadFromJSON();
    }

    /**
     * Loads morphs from JSON 
     */
    private void loadFromJSON()
    {
        Morph chicken = new Morph();

        chicken.abilities = new IAbility[] {abilities.get("glide")};
        chicken.model = Metamorph.proxy.models.models.get("Chicken");

        Morph sheep = new Morph();

        sheep.abilities = new IAbility[] {abilities.get("climb")};
        sheep.model = Metamorph.proxy.models.models.get("Sheep");

        /* For now, only hardcoded morphs */
        this.morphs.put("Chicken", chicken);
        this.morphs.put("Sheep", sheep);
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