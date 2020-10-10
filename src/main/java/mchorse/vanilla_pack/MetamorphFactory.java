package mchorse.vanilla_pack;

import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.abilities.IAbility;
import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.abilities.IAttackAbility;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.vanilla_pack.abilities.Climb;
import mchorse.vanilla_pack.abilities.FireProof;
import mchorse.vanilla_pack.abilities.Fly;
import mchorse.vanilla_pack.abilities.Glide;
import mchorse.vanilla_pack.abilities.Hungerless;
import mchorse.vanilla_pack.abilities.Jumping;
import mchorse.vanilla_pack.abilities.NightVision;
import mchorse.vanilla_pack.abilities.PreventFall;
import mchorse.vanilla_pack.abilities.Rotten;
import mchorse.vanilla_pack.abilities.SnowWalk;
import mchorse.vanilla_pack.abilities.StepUp;
import mchorse.vanilla_pack.abilities.SunAllergy;
import mchorse.vanilla_pack.abilities.Swim;
import mchorse.vanilla_pack.abilities.WaterAllergy;
import mchorse.vanilla_pack.abilities.WaterBreath;
import mchorse.vanilla_pack.actions.Endermite;
import mchorse.vanilla_pack.actions.Explode;
import mchorse.vanilla_pack.actions.FireBreath;
import mchorse.vanilla_pack.actions.Fireball;
import mchorse.vanilla_pack.actions.Jump;
import mchorse.vanilla_pack.actions.Potions;
import mchorse.vanilla_pack.actions.ShulkerBullet;
import mchorse.vanilla_pack.actions.Sliverfish;
import mchorse.vanilla_pack.actions.SmallFireball;
import mchorse.vanilla_pack.actions.Snowball;
import mchorse.vanilla_pack.actions.Spit;
import mchorse.vanilla_pack.actions.Teleport;
import mchorse.vanilla_pack.attacks.KnockbackAttack;
import mchorse.vanilla_pack.attacks.MobAttack;
import mchorse.vanilla_pack.attacks.PoisonAttack;
import mchorse.vanilla_pack.attacks.WitherAttack;
import mchorse.vanilla_pack.editors.GuiBlockMorph;
import mchorse.vanilla_pack.editors.GuiEntityMorph;
import mchorse.vanilla_pack.editors.GuiItemMorph;
import mchorse.vanilla_pack.editors.GuiLabelMorph;
import mchorse.vanilla_pack.editors.GuiPlayerMorph;
import mchorse.vanilla_pack.morphs.BlockMorph;
import mchorse.vanilla_pack.morphs.IronGolemMorph;
import mchorse.vanilla_pack.morphs.ItemMorph;
import mchorse.vanilla_pack.morphs.LabelMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import mchorse.vanilla_pack.morphs.ShulkerMorph;
import mchorse.vanilla_pack.morphs.UndeadMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

/**
 * Metamorph factory
 * 
 * This is underlying morph factory. It's responsible for registering vanilla actions
 * and morph sections
 */
public class MetamorphFactory implements IMorphFactory
{
    /**
     * Nothing to register here, since all of the morphs are generated on 
     * runtime 
     */
    @Override
    public void register(MorphManager manager)
    {
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
        abilities.put("rotten", new Rotten());
        abilities.put("snow_walk", new SnowWalk());
        abilities.put("step_up", new StepUp());
        abilities.put("sun_allergy", new SunAllergy());
        abilities.put("swim", new Swim());
        abilities.put("water_allergy", new WaterAllergy());
        abilities.put("water_breath", new WaterBreath());

        /* Register default actions */
        actions.put("endermite", new Endermite());
        actions.put("explode", new Explode());
        actions.put("fireball", new Fireball());
        actions.put("fire_breath", new FireBreath());
        actions.put("jump", new Jump());
        actions.put("potions", new Potions());
        actions.put("shulker_bullet", new ShulkerBullet());
        actions.put("silverfish", new Sliverfish());
        actions.put("small_fireball", new SmallFireball());
        actions.put("snowball", new Snowball());
        actions.put("spit", new Spit());
        actions.put("teleport", new Teleport());

        /* Register default attacks */
        attacks.put("knockback", new KnockbackAttack());
        attacks.put("mob", new MobAttack());
        attacks.put("poison", new PoisonAttack());
        attacks.put("wither", new WitherAttack());

        /* Register main section */
        manager.list.register(new MetamorphSection(this, "entity"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMorphEditors(Minecraft mc, List<GuiAbstractMorph> editors)
    {
        editors.add(new GuiLabelMorph(mc));
        editors.add(new GuiItemMorph(mc));
        editors.add(new GuiBlockMorph(mc));
        editors.add(new GuiPlayerMorph(mc));
        editors.add(new GuiEntityMorph(mc));
        editors.add(new GuiAbstractMorph(mc));
    }

    /**
     * Checks if the {@link EntityList} has an entity with given name does 
     * exist and the entity is a living base.
     */
    @Override
    public boolean hasMorph(String name)
    {
        if (name.equalsIgnoreCase("player"))
        {
            return true;
        }

        if (name.equals("block") || name.equals("item") || name.equals("label"))
        {
            return true;
        }

        Class<? extends Entity> clazz = null;
        ResourceLocation key = new ResourceLocation(name);

        for (EntityEntry entity : ForgeRegistries.ENTITIES)
        {
            if (entity.getRegistryName().equals(key))
            {
                clazz = entity.getEntityClass();
            }
        }

        return clazz == null ? false : EntityLivingBase.class.isAssignableFrom(clazz);
    }

    /**
     * Create an {@link EntityMorph} from NBT
     */
    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        String name = tag.getString("Name");
        AbstractMorph morph;

        if (tag.getString("Name").equalsIgnoreCase("player"))
        {
            PlayerMorph player = new PlayerMorph();

            player.fromNBT(tag);

            return player.profile != null ? player : null;
        }

        if (name.equals("block"))
        {
            morph = new BlockMorph();
        }
        else if (name.equals("item"))
        {
            morph = new ItemMorph();
        }
        else if (name.equals("label"))
        {
            morph = new LabelMorph();
        }
        else
        {
            morph = morphFromName(name);
        }

        morph.fromNBT(tag);

        return morph;
    }

    /**
     * Get a morph from a name 
     */
    public EntityMorph morphFromName(String name)
    {
        if (name.equals("minecraft:zombie") || name.equals("minecraft:skeleton") || name.equals("minecraft:zombie_villager"))
        {
            return new UndeadMorph();
        }
        else if (name.equals("minecraft:villager_golem"))
        {
            return new IronGolemMorph();
        }
        else if (name.equals("minecraft:shulker"))
        {
            return new ShulkerMorph();
        }

        return new EntityMorph();
    }
}
