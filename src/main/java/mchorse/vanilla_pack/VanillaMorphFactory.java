package mchorse.vanilla_pack;

import java.util.HashMap;
import java.util.Map;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.models.ModelManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.CustomMorph;
import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.model.parsing.ModelParser;
import mchorse.vanilla_pack.abilities.Climb;
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
import mchorse.vanilla_pack.morphs.BlazeMorph;
import net.minecraft.client.model.ModelBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
        /* Blacklist morph */
        manager.blacklist.add("metamorph.Morph");

        /* Define shortcuts */
        Map<String, IAbility> abilities = manager.abilities;
        Map<String, IAttackAbility> attacks = manager.attacks;
        Map<String, IAction> actions = manager.actions;

        /* Register default abilities */
        abilities.put("climb", new Climb());
        abilities.put("fire_proof", new FireProof());
        abilities.put("fly", new Fly());
        abilities.put("glide", new Glide());
        abilities.put("hungerless", new Hungerless());
        abilities.put("jumping", new Jumping());
        abilities.put("night_vision", new NightVision());
        abilities.put("prevent_fall", new PreventFall());
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
        this.registerMorphsSettings(manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {
        for (AbstractMorph morph : this.morphs.values())
        {
            morph.renderer = ClientProxy.modelRenderer;
        }

        this.registerClientModels(manager.models);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(String morphName)
    {
        return null;
    }

    @Override
    public void getMorphs(MorphList morphs, World world)
    {
        for (CustomMorph morph : this.morphs.values())
        {
            morphs.addMorph(morph.name, "patched_vanilla", morph.clone());
        }
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
     * Register morph settings from JSON file
     * 
     * This method is responsible for registering all JSON settings for vanilla 
     * patched morphs
     */
    protected void registerMorphsSettings(MorphManager manager)
    {
        MorphUtils.loadMorphSettings(manager, this.getClass().getClassLoader().getResourceAsStream("assets/metamorph/morphs.json"));
    }

    /**
     * Load served based custom models
     */
    private void registerModels(ModelManager models)
    {
        /* Hostile mobs */
        this.loadModel(models, "Blaze");
        this.loadModel(models, "Creeper");
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

            CustomMorph morph = model.equals("Blaze") ? new BlazeMorph() : new CustomMorph();

            morph.name = model;
            morph.model = models.models.get(model);

            this.morphs.put(model, morph);
        }
        catch (Exception e)
        {
            System.out.println("An exception was raised when loading '" + model + "' model!");
            e.printStackTrace();
        }
    }

    /**
     * Turn all registered custom models into client {@link ModelBase}d models. 
     */
    @SideOnly(Side.CLIENT)
    private void registerClientModels(ModelManager models)
    {
        for (String model : this.morphs.keySet())
        {
            this.loadClientModel(models, model);
        }
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