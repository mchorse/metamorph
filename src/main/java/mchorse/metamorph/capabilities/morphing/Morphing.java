package mchorse.metamorph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;

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
    private AbstractMorph morph;

    public static IMorphing get(EntityPlayer player)
    {
        return player.getCapability(MorphingProvider.MORPHING_CAP, null);
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
        return this.morph;
    }

    @Override
    public void setCurrentMorph(AbstractMorph morph, EntityPlayer player, boolean force)
    {
        if (morph == null)
        {
            this.demorph(player);

            return;
        }

        boolean creative = player != null ? player.isCreative() : false;

        if (force || creative || this.acquiredMorph(morph))
        {
            if (player != null && this.morph != null)
            {
                this.morph.demorph(player);
            }

            this.morph = morph;

            if (player != null)
            {
                this.morph.morph(player);
            }
        }
    }

    @Override
    public void demorph(EntityPlayer player)
    {
        if (player != null && this.morph != null)
        {
            this.morph.demorph(player);
        }

        this.morph = null;
    }

    @Override
    public boolean isMorphed()
    {
        return this.morph != null;
    }

    @Override
    public void copy(IMorphing morphing, EntityPlayer player)
    {
        this.acquiredMorphs = morphing.getAcquiredMorphs();
        this.setCurrentMorph(morphing.getCurrentMorph(), player, true);
    }
}