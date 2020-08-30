package mchorse.metamorph.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Morph settings
 * 
 * An instance of this class is responsible for storing information about 
 * morph's configurable settings.
 * 
 * For well-defined defaults, use {@link #DEFAULT_MORPHED} or {@link #DEFAULT}.
 */
public class MorphSettings
{
    /**
     * "Safe" settings, equivalent to not being morphed
     */
    public static final MorphSettings DEFAULT = new MorphSettings();
    
    static
    {
        DEFAULT.hostile = false;
        DEFAULT.hands = true;
        DEFAULT.updates = false;
    }

    /**
     * Default settings to fall back on for most morphs.
     */
    public static final MorphSettings DEFAULT_MORPHED = new MorphSettings();

    /**
     * Abilities that are going to be applied on a morph 
     */
    public IAbility[] abilities = new IAbility[]{};
    public boolean hasAbilities = true;

    /**
     * Attack that is going to be used on a morph
     */
    public IAttackAbility attack = null;
    public boolean hasAttack = true;

    /**
     * Action that is going to be used on a morph
     */
    public IAction action = null;
    public boolean hasAction = true;

    /**
     * Health units which are going to be applied
     */
    public int health = 20;
    public boolean hasHealth = true;

    /**
     * Speed which is going to be applied 
     */
    public float speed = 0.1F;
    public boolean hasSpeed = true;

    /**
     * Hostile flag which is going to be applied
     */
    public boolean hostile = true;
    public boolean hasHostile = true;

    /**
     * Does client tries render hands for this morph 
     */
    public boolean hands = false;
    public boolean hasHands = true;

    /**
     * Does this morph updates itself 
     */
    public boolean updates = true;
    public boolean hasUpdates = true;

    /**
     * Merge given morph settings with this settings 
     */
    public void merge(MorphSettings setting)
    {
        if (setting.abilities != null)
        {
            List<IAbility> abilities = new ArrayList<IAbility>();
            if (setting.abilities.length != 0)
            {
                for (IAbility ability : setting.abilities)
                {
                    abilities.add(ability);
                }
            }
            this.abilities = abilities.toArray(new IAbility[abilities.size()]);
        }
        else
        {
            this.abilities = null;
        }
        this.hasAbilities = setting.hasAbilities;

        this.action = setting.action;
        this.hasAction = setting.hasAction;
        this.attack = setting.attack;
        this.hasAttack = setting.hasAttack;

        this.health = setting.health;
        this.hasHealth = setting.hasHealth;
        this.speed = setting.speed;
        this.hasSpeed = setting.hasSpeed;
        this.hostile = setting.hostile;
        this.hasHostile = setting.hasHostile;
        this.hands = setting.hands;
        this.hasHands = setting.hasHands;
        this.updates = setting.updates;
        this.hasUpdates = setting.hasUpdates;
    }

    /**
     * Apply any additional settings to this one as long as they are not null/empty
     */
    public void applyOverrides(MorphSettings setting)
    {
        if (setting.hasAbilities)
        {
            if (setting.abilities != null)
            {
                List<IAbility> abilities = new ArrayList<IAbility>();
    
                for (IAbility ability : setting.abilities)
                {
                    abilities.add(ability);
                }
    
                this.abilities = abilities.toArray(new IAbility[abilities.size()]);
            }
            else
            {
                this.abilities = new IAbility[]{};
            }
            this.hasAbilities = true;
        }

        if (setting.hasAction)
        {
            this.action = setting.action;
            this.hasAction = true;
        }
        if (setting.hasAttack)
        {
            this.attack = setting.attack;
            this.hasAttack = true;
        }

        if (setting.hasHealth)
        {
            this.health = setting.health;
            this.hasHealth = true;
        }
        if (setting.hasSpeed)
        {
            this.speed = setting.speed;
            this.hasSpeed = true;
        }
        if (setting.hasHostile)
        {
            this.hostile = setting.hostile;
            this.hasHostile = true;
        }
        if (setting.hasHands)
        {
            this.hands = setting.hands;
            this.hasHands = true;
        }
        if (setting.hasUpdates)
        {
            this.updates = setting.updates;
            this.hasUpdates = true;
        }
    }

    @Override
    public MorphSettings clone()
    {
        MorphSettings settings = new MorphSettings();

        settings.merge(this);

        return settings;
    }

    /**
     * Write morph settings to the network buffer
     */
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.hasAbilities);
        if (this.hasAbilities)
        {
            buf.writeInt(this.abilities.length);
    
            for (IAbility ability : this.abilities)
            {
                String string = getKey(MorphManager.INSTANCE.abilities, ability);
    
                ByteBufUtils.writeUTF8String(buf, string == null ? "" : string);
            }
        }

        String action = getKey(MorphManager.INSTANCE.actions, this.action);
        String attack = getKey(MorphManager.INSTANCE.attacks, this.attack);

        buf.writeBoolean(this.hasAction);
        if (this.hasAction)
        {
            buf.writeBoolean(action != null);
            if (action != null)
            {
                ByteBufUtils.writeUTF8String(buf, action);
            }
        }

        buf.writeBoolean(this.hasAttack);
        if (this.hasAttack) {
            buf.writeBoolean(attack != null);
            if (attack != null)
            {
                ByteBufUtils.writeUTF8String(buf, attack);
            }
        }
        
        buf.writeBoolean(this.hasHealth);
        if (this.hasHealth)
        {
            buf.writeInt(this.health);
        }
        buf.writeBoolean(this.hasSpeed);
        if (this.hasSpeed)
        {
            buf.writeFloat(this.speed);
        }
        buf.writeBoolean(this.hasHostile);
        if (this.hasHostile)
        {
            buf.writeBoolean(this.hostile);
        }
        buf.writeBoolean(this.hasHands);
        if (this.hasHands)
        {
            buf.writeBoolean(this.hands);
        }
        buf.writeBoolean(this.hasUpdates);
        if (this.hasUpdates)
        {
            buf.writeBoolean(this.updates);
        }
    }

    /**
     * Read morph settings from the network buffer 
     */
    public void fromBytes(ByteBuf buf)
    {
        this.hasAbilities = buf.readBoolean();
        if (this.hasAbilities)
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
        }

        this.hasAction = buf.readBoolean();
        if (this.hasAction)
        {
            if (buf.readBoolean())
            {
                String action = ByteBufUtils.readUTF8String(buf);
                this.action = MorphManager.INSTANCE.actions.get(action);
            }
            else
            {
                this.action = null;
            }
        }

        this.hasAttack = buf.readBoolean();
        if (this.hasAttack)
        {
            if (buf.readBoolean())
            {
                String attack = ByteBufUtils.readUTF8String(buf);
                this.attack = MorphManager.INSTANCE.attacks.get(attack);
            }
            else
            {
                this.attack = null;
            }
        }

        this.hasHealth = buf.readBoolean();
        if (this.hasHealth)
        {
            this.health = buf.readInt();
        }
        this.hasSpeed = buf.readBoolean();
        if (this.hasSpeed)
        {
            this.speed = buf.readFloat();
        }
        this.hasHostile = buf.readBoolean();
        if (this.hasHostile)
        {
            this.hostile = buf.readBoolean();
        }
        this.hasHands = buf.readBoolean();
        if (this.hasHands)
        {
            this.hands = buf.readBoolean();
        }
        this.hasUpdates = buf.readBoolean();
        if (this.hasUpdates)
        {
            this.updates = buf.readBoolean();
        }
    }

    /**
     * Get key of given value in given map 
     */
    public static <T> String getKey(Map<String, T> map, T value)
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