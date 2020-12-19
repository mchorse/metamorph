package mchorse.metamorph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Default implementation of {@link IMorphing} interface.
 *
 * This class is responsible for storing current morphing, setting and retrieval
 * of current morphing.
 */
public class Morphing implements IMorphing
{
    /**
     * List of acquired abstract morphs 
     */
    private List<AbstractMorph> acquiredMorphs = new ArrayList<AbstractMorph>();

    /**
     * Current used morph
     */
    private Morph morph = new Morph();

    /**
     * Used for animation
     */
    private AbstractMorph previousMorph;

    /**
     * Animation timer 
     */
    private int animation;

    /**
     * The last damage source received by the player
     */
    private DamageSource lastDamageSource;

    /**
     * (health / max health) is stored here when the new max health ends up
     * very close to zero, and retrieved when the fraction is meaningful again
     */
    private float lastHealthRatio;

    /**
     * Whether or not the current player is in a morph which can drown on land
     * due to having the Swim ability
     */
    private boolean hasSquidAir = false;

    /**
     * The air value used for morphs with the Swim ability in place of regular
     * player air
     */
    private int squidAir = 300;

    /**
     * Last health that player had before morphing, should fix issue that people complain about
     */
    private float lastHealth;

    public static IMorphing get(EntityPlayer player)
    {
        if (player == null)
        {
            return null;
        }

        return player.getCapability(MorphingProvider.MORPHING_CAP, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isAnimating()
    {
        if (Metamorph.disableMorphAnimation.get())
        {
            return false;
        }

        return this.animation != -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getAnimation()
    {
        return this.animation;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AbstractMorph getPreviousMorph()
    {
        return this.previousMorph;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderPlayer(EntityPlayer player, double x, double y, double z, float yaw, float partialTick)
    {
        if (player.isSpectator())
        {
            return false;
        }

        if (this.morph.isEmpty() && !this.isAnimating())
        {
            return false;
        }

        if (this.morph.isEmpty() && this.animation <= 10 || this.previousMorph == null && this.animation > 10)
        {
            return false;
        }

        if (!this.isAnimating())
        {
            if (MorphUtils.render(this.morph.get(), player, x, y, z, yaw, partialTick))
            {
                return true;
            }
        }

        GlStateManager.pushMatrix();

        /* Render morph in after transition */
        if (this.animation <= 10)
        {
            float anim = (this.animation - partialTick) / 10.0F;
            float offset = 0;

            if (anim >= 0)
            {
                offset = -anim * anim * 2F;
            }

            GlStateManager.translate(x, y + offset, z);

            if (anim >= 0)
            {
                GlStateManager.rotate(anim * -90.0F, 1, 0, 0);
                GlStateManager.scale(1 - anim, 1 - anim, 1 - anim);
            }

            MorphUtils.render(this.morph.get(), player, 0, 0, 0, yaw, partialTick);
        }
        else if (this.previousMorph != null)
        {
            /* Render morph before the transition */
            float anim = (this.animation - 10 - partialTick) / 10.0F;
            float offset = 0;

            if (anim >= 0)
            {
                offset = (1 - anim);
            }

            GlStateManager.translate(x, y + offset, z);

            if (anim >= 0)
            {
                GlStateManager.rotate((1 - anim) * 90.0F, 1, 0, 0);
                GlStateManager.scale(anim, anim, anim);
            }

            MorphUtils.render(this.previousMorph, player, 0, 0, 0, yaw, partialTick);
        }

        GlStateManager.popMatrix();

        return true;
    }

    @Override
    public DamageSource getLastDamageSource()
    {
        return lastDamageSource;
    }

    @Override
    public void setLastDamageSource(DamageSource damageSource)
    {
        this.lastDamageSource = damageSource;
    }

    @Override
    public boolean acquireMorph(AbstractMorph morph)
    {
        if (morph == null || this.acquiredMorph(morph))
        {
            return false;
        }

        this.acquiredMorphs.add(morph);

        return true;
    }

    @Override
    public boolean acquiredMorph(AbstractMorph morph)
    {
        for (AbstractMorph acquired : this.acquiredMorphs)
        {
            if (acquired.equals(morph))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<AbstractMorph> getAcquiredMorphs()
    {
        return acquiredMorphs;
    }

    @Override
    public void setAcquiredMorphs(List<AbstractMorph> morphs)
    {
        this.acquiredMorphs.clear();
        this.acquiredMorphs.addAll(morphs);
    }

    @Override
    public AbstractMorph getCurrentMorph()
    {
        return this.morph.get();
    }

    @Override
    public boolean setCurrentMorph(AbstractMorph morph, EntityPlayer player, boolean force)
    {
        if (morph == null)
        {
            this.demorph(player);

            return true;
        }

        boolean creative = player != null && player.isCreative();

        if (force || creative || this.acquiredMorph(morph))
        {
            if (player != null)
            {
                if (this.morph.isEmpty())
                {
                    this.lastHealth = (float) player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                }
                else
                {
                    this.morph.get().demorph(player);
                }
            }

            this.setMorph(morph);

            if (player != null && !this.morph.isEmpty())
            {
                AbstractMorph current = this.morph.get();

                this.setHealth(player, current.settings.health);
                current.morph(player);
            }

            return true;
        }

        return false;
    }

    @Override
    public void demorph(EntityPlayer player)
    {
        if (player != null && !this.morph.isEmpty())
        {
            this.morph.get().demorph(player);
        }

        if (player != null)
        {
            /* 20 is default player's health */
            this.setHealth(player, this.lastHealth <= 0.0F ? 20.0F : this.lastHealth);
        }

        this.setMorph(null);
    }

    /**
     * Set current morph, as well as update animation information  
     */
    protected void setMorph(AbstractMorph morph)
    {
        AbstractMorph previous = this.morph.get();

        if (this.morph.set(morph))
        {
            if (!Metamorph.disableMorphAnimation.get())
            {
                this.animation = 20;
            }

            this.previousMorph = previous;
        }
    }

    @Override
    public boolean isMorphed()
    {
        return !this.morph.isEmpty();
    }

    @Override
    public void favorite(int index)
    {
        if (index >= 0 && index < this.acquiredMorphs.size())
        {
            AbstractMorph morph = this.acquiredMorphs.get(index);

            morph.favorite = !morph.favorite;
        }
    }

    @Override
    public void keybind(int index, int keycode)
    {
        if (index >= 0 && index < this.acquiredMorphs.size())
        {
            AbstractMorph morph = this.acquiredMorphs.get(index);

            morph.keybind = keycode;
        }
    }

    @Override
    public boolean remove(int index)
    {
        if (index >= 0 && index < this.acquiredMorphs.size())
        {
            this.acquiredMorphs.remove(index);

            return true;
        }

        return false;
    }

    @Override
    public void removeAcquired()
    {
        this.acquiredMorphs.clear();
    }

    @Override
    public void copy(IMorphing morphing, EntityPlayer player)
    {
        this.acquiredMorphs.addAll(morphing.getAcquiredMorphs());

        if (morphing.getCurrentMorph() != null)
        {
            this.setCurrentMorph(morphing.getCurrentMorph().copy(), player, true);
        }
        else
        {
            this.setCurrentMorph(null, player, true);
        }
    }

    @Override
    public float getLastHealthRatio()
    {
        return lastHealthRatio;
    }

    @Override
    public void setLastHealthRatio(float lastHealthRatio)
    {
        this.lastHealthRatio = lastHealthRatio;
    }

    @Override
    public boolean getHasSquidAir()
    {
        return hasSquidAir;
    }

    @Override
    public void setHasSquidAir(boolean hasSquidAir)
    {
        this.hasSquidAir = hasSquidAir;
    }

    @Override
    public int getSquidAir()
    {
        return squidAir;
    }

    @Override
    public void setSquidAir(int squidAir)
    {
        this.squidAir = squidAir;
    }

    @Override
    public float getLastHealth()
    {
        return this.lastHealth;
    }

    @Override
    public void setLastHealth(float lastHealth)
    {
        this.lastHealth = lastHealth;
    }

    @Override
    public void update(EntityPlayer player)
    {
        if (this.animation >= 0)
        {
            this.animation--;
        }

        if (this.animation == 16 && !player.world.isRemote && !Metamorph.disableMorphAnimation.get())
        {
            /* Pop! */
            ((WorldServer) player.world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, false, player.posX, player.posY + 0.5, player.posZ, 25, 0.5, 0.5, 0.5, 0.05);

            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
        }

        if (!this.morph.isEmpty())
        {
            AbstractMorph morph = this.morph.get();

            if (!Metamorph.disableHealth.get())
            {
                this.setMaxHealth(player, morph.settings.health);
            }

            morph.update(player);
        }
    }

    /* Adjusting health */

    /**
     * Set player's health proportional to the current health with given max
     * health.
     *
     * @author asanetargoss
     */
    protected void setHealth(EntityLivingBase target, float health)
    {
        if (Metamorph.disableHealth.get())
        {
            return;
        }

        float maxHealth = target.getMaxHealth();
        float currentHealth = target.getHealth();
        float ratio = currentHealth / maxHealth;

        // A sanity check to prevent "healing" health when morphing to and from
        // a mob with essentially zero health
        if (target instanceof EntityPlayer)
        {
            IMorphing capability = Morphing.get((EntityPlayer) target);
            if (capability != null)
            {
                // Check if a health ratio makes sense for the old health value
                if (maxHealth > IMorphing.REASONABLE_HEALTH_VALUE)
                {
                    // If it makes sense, store that ratio in the capability
                    capability.setLastHealthRatio(ratio);
                }
                else if (health > IMorphing.REASONABLE_HEALTH_VALUE)
                {
                    // If it doesn't make sense, BUT the new max health makes
                    // sense, retrieve the ratio from the capability and use that instead
                    ratio = capability.getLastHealthRatio();
                }
            }
        }

        this.setMaxHealth(target, health);
        // We need to retrieve the max health of the target after modifiers are
        // applied to get a sensible value
        float proportionalHealth = target.getMaxHealth() * ratio;
        target.setHealth(proportionalHealth <= 0.0F ? Float.MIN_VALUE : proportionalHealth);
    }

    /**
     * Set player's max health
     */
    protected void setMaxHealth(EntityLivingBase target, float health)
    {
        if (target.getMaxHealth() != health)
        {
            target.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        }
    }
}