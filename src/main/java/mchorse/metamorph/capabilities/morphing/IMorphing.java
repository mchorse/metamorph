package mchorse.metamorph.capabilities.morphing;

import java.util.List;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Morphing interface
 *
 * This interface is responsible for morphing. See {@link Morphing} class for
 * default implementation.
 */
public interface IMorphing
{
    public static final float REASONABLE_HEALTH_VALUE = Float.MIN_VALUE * 100;

    /**
     * Whether this morph is in the process of animation 
     */
    public boolean isAnimating();

    /**
     * Get animation tick
     */
    @SideOnly(Side.CLIENT)
    public int getAnimation();

    /**
     * Get previous animation morph 
     */
    @SideOnly(Side.CLIENT)
    public AbstractMorph getPreviousMorph();

    /**
     * Render player as a morph
     */
    @SideOnly(Side.CLIENT)
    public boolean renderPlayer(EntityPlayer player, double x, double y, double z, float yaw, float partialTick);

    /**
     * Add a morph
     */
    public boolean acquireMorph(AbstractMorph morph);

    /**
     * Check if this capability has acquired a morph
     */
    public boolean acquiredMorph(AbstractMorph morph);

    /**
     * Get all acquired morph
     */
    public List<AbstractMorph> getAcquiredMorphs();

    /**
     * Set acquired morph
     */
    public void setAcquiredMorphs(List<AbstractMorph> morphs);

    /**
     * Get current morph 
     */
    public AbstractMorph getCurrentMorph();

    /**
     * Set current morph
     */
    public boolean setCurrentMorph(AbstractMorph morph, EntityPlayer player, boolean force);

    /**
     * Demorph this capability 
     */
    public void demorph(EntityPlayer player);

    /**
     * Is this capability is morphed at all 
     */
    public boolean isMorphed();

    /**
     * Favorite or unfavorite a morph by given index
     * 
     * @return if true then given favorite was added, or false if it was 
     *         removed
     */
    public boolean favorite(int index);

    /**
     * Remove a morph at given index
     */
    public boolean remove(int index);

    /**
     * Copy data from other morph 
     */
    public void copy(IMorphing morphing, EntityPlayer player);

    /**
     * Get the last recorded finite health fraction of the player
     */
    public float getLastHealthRatio();

    /**
     * Determines what the player's new health will be if the player morphs out of a morph with very low health
     */
    public void setLastHealthRatio(float lastHealthRatio);

    /**
     * Update the player 
     */
    public void update(EntityPlayer player);
}