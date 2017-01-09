package mchorse.metamorph.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.morphs.AbstractMorph;

/**
 * Morph settings
 * 
 * An instance of this class is responsible for storing information about 
 * morph's configurable settings.
 */
public class MorphSettings
{
    /**
     * Abilities that are going to be applied on a morph 
     */
    public IAbility[] abilities = new IAbility[] {};

    /**
     * Attack that is going to be used on a morph
     */
    public IAttackAbility attack;

    /**
     * Action that is going to be used on a morph
     */
    public IAction action;

    /**
     * Health units which are going to be applied
     */
    public int health = 20;

    /**
     * Speed which is going to be applied 
     */
    public float speed = 0.1F;

    /**
     * Hostile flag which is going to be applied
     */
    public boolean hostile;

    /**
     * This field is responsible for storing custom data for people who want to 
     * provide more options that aren't hardcoded in this class. 
     */
    public Map<String, Object> customData = new HashMap<String, Object>();

    /**
     * Apply this morph settings on an abstract morph.
     * 
     * Another option would be place this method in 
     */
    public void apply(AbstractMorph morph)
    {
        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.health = this.health;
        morph.speed = this.speed;
        morph.hostile = this.hostile;
    }

    /**
     * Merge given morph settings with this settings 
     */
    public void merge(MorphSettings setting)
    {
        if (setting.abilities.length != 0)
        {
            List<IAbility> abilities = new ArrayList<IAbility>();

            for (IAbility ability : this.abilities)
            {
                abilities.add(ability);
            }

            for (IAbility ability : setting.abilities)
            {
                if (!abilities.contains(ability))
                {
                    abilities.add(ability);
                }
            }

            this.abilities = abilities.toArray(new IAbility[abilities.size()]);
        }

        if (setting.action != null)
        {
            this.action = setting.action;
        }

        if (setting.attack != null)
        {
            this.attack = setting.attack;
        }

        if (setting.health != 20)
        {
            this.health = setting.health;
        }

        if (setting.speed != 0.1)
        {
            this.speed = setting.speed;
        }

        this.hostile = setting.hostile;
        this.customData.putAll(setting.customData);
    }
}