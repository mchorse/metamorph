package mchorse.metamorph.api.morph;

import java.util.HashMap;
import java.util.Map;

import mchorse.metamorph.api.IAbility;
import mchorse.metamorph.api.IAction;
import mchorse.metamorph.api.IAttackAbility;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    /**
     * That's a singleton, boy! 
     */
    private MorphManager()
    {}

    /**
     * Get morph from the entity
     * 
     * Here I should add some kind of mechanism that allows people to substitute 
     * the name of the morph based on the given entity (in the future with 
     * introduction of the public API).
     */
    public String morphNameFromEntity(Entity entity)
    {
        if (entity instanceof EntitySkeleton)
        {
            SkeletonType skeleton = ((EntitySkeleton) entity).func_189771_df();

            if (skeleton.equals(SkeletonType.WITHER))
            {
                return "WitherSkeleton";
            }
        }

        return EntityList.getEntityString(entity);
    }

    /**
     * Get display name for morph (only client)
     */
    @SideOnly(Side.CLIENT)
    public String morphDisplayNameFromMorph(String morph)
    {
        if (morph.equals("WitherSkeleton"))
        {
            morph = "Skeleton";
        }

        String key = "entity." + morph + ".name";
        String result = I18n.format(key);

        return key.equals(result) ? morph : result;
    }

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