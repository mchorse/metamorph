package mchorse.vanilla_pack;

import java.util.Map;

import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.morphs.AbstractMorph;
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
import mchorse.vanilla_pack.actions.FireBreath;
import mchorse.vanilla_pack.actions.Fireball;
import mchorse.vanilla_pack.actions.Jump;
import mchorse.vanilla_pack.actions.Potions;
import mchorse.vanilla_pack.actions.SmallFireball;
import mchorse.vanilla_pack.actions.Snowball;
import mchorse.vanilla_pack.actions.Teleport;
import mchorse.vanilla_pack.attacks.KnockbackAttack;
import mchorse.vanilla_pack.attacks.PoisonAttack;
import mchorse.vanilla_pack.attacks.WitherAttack;
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
        actions.put("fire_breath", new FireBreath());
        actions.put("jump", new Jump());
        actions.put("potions", new Potions());
        actions.put("small_fireball", new SmallFireball());
        actions.put("snowball", new Snowball());
        actions.put("teleport", new Teleport());

        /* Register default attacks */
        attacks.put("poison", new PoisonAttack());
        attacks.put("wither", new WitherAttack());
        attacks.put("knockback", new KnockbackAttack());

        this.registerMorphsSettings(manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morph)
    {
        return null;
    }

    @Override
    public void getMorphs(MorphList morphs, World world)
    {}

    @Override
    public boolean hasMorph(String name)
    {
        return false;
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        return null;
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
}