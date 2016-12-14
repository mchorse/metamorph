package mchorse.metamorph.capabilities.morphing;

import java.util.List;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Morphing interface
 *
 * This interface is responsible for morphing. See {@link Morphing} class for
 * default implementation.
 */
public interface IMorphing
{
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
    public void setCurrentMorph(AbstractMorph morph, EntityPlayer player, boolean force);

    /**
     * Demorph this capability 
     */
    public void demorph(EntityPlayer player);

    /**
     * Is this capability is morphed at all 
     */
    public boolean isMorphed();

    /**
     * Copy data from other morph 
     */
    public void copy(IMorphing morphing, EntityPlayer player);
}