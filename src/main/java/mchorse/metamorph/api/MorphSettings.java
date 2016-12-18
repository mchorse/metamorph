package mchorse.metamorph.api;

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
    public IAbility[] abilities = new IAbility[] {};

    public IAttackAbility attack;

    public IAction action;

    public int health = 20;

    public float speed = 0.1F;

    public boolean hostile;

    /**
     * This field is responsible for storing custom data for people who want to 
     * provide more options that aren't hardcoded in this class. 
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Object> customData;

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
}