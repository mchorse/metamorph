package mchorse.metamorph.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
     * Does client tries render hands for this morph 
     */
    public boolean hands;

    /**
     * This field is responsible for storing custom data for people who want to 
     * provide more options that aren't hardcoded in this class. 
     * 
     * I don't know how it's going to work, though.
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
        morph.hands = this.hands;
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
        this.hands = setting.hands;
        this.customData.putAll(setting.customData);
    }

    /**
     * Write morph settings to the network buffer
     */
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.abilities.length);

        for (IAbility ability : this.abilities)
        {
            String string = this.getKey(MorphManager.INSTANCE.abilities, ability);

            ByteBufUtils.writeUTF8String(buf, string == null ? "" : string);
        }

        String action = this.getKey(MorphManager.INSTANCE.actions, this.action);
        String attack = this.getKey(MorphManager.INSTANCE.attacks, this.attack);

        buf.writeBoolean(action != null);

        if (action != null)
        {
            ByteBufUtils.writeUTF8String(buf, action);
        }

        buf.writeBoolean(attack != null);

        if (attack != null)
        {
            ByteBufUtils.writeUTF8String(buf, attack);
        }

        buf.writeInt(this.health);
        buf.writeFloat(this.speed);
        buf.writeBoolean(this.hostile);
        buf.writeBoolean(this.hands);
    }

    /**
     * Read morph settings from the network buffer 
     */
    public void fromBytes(ByteBuf buf)
    {
        List<IAbility> abilities = new ArrayList<IAbility>();

        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            IAbility ability = MorphManager.INSTANCE.abilities.get(ByteBufUtils.readUTF8String(buf));

            if (ability != null)
            {
                abilities.add(ability);
            }
        }

        this.abilities = abilities.toArray(new IAbility[abilities.size()]);

        if (buf.readBoolean())
        {
            String action = ByteBufUtils.readUTF8String(buf);

            this.action = MorphManager.INSTANCE.actions.get(action);
        }

        if (buf.readBoolean())
        {
            String attack = ByteBufUtils.readUTF8String(buf);

            this.attack = MorphManager.INSTANCE.attacks.get(attack);
        }

        this.health = buf.readInt();
        this.speed = buf.readFloat();
        this.hostile = buf.readBoolean();
        this.hands = buf.readBoolean();
    }

    /**
     * Get key of given value in given map 
     */
    public <T> String getKey(Map<String, T> map, T value)
    {
        if (value == null)
        {
            return null;
        }

        for (Map.Entry<String, T> entry : map.entrySet())
        {
            if (entry.getValue() == value)
            {
                return entry.getKey();
            }
        }

        return null;
    }
}