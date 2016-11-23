package mchorse.metamorph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.api.morph.Morph;
import mchorse.metamorph.api.morph.MorphManager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Default implementation of {@link IMorphing} interface.
 *
 * This class is responsible for storing current morphing, setting and retrieval
 * of current morphing.
 */
public class Morphing implements IMorphing
{
    private List<String> acquiredMorphs = new ArrayList<String>();

    private Morph morph;
    private String name = "";

    public static IMorphing get(EntityPlayer player)
    {
        return player.getCapability(MorphingProvider.MORPHING_CAP, null);
    }

    @Override
    public boolean acquireMorph(String name)
    {
        Morph morph = MorphManager.INSTANCE.morphs.get(name);

        if (morph == null || this.acquiredMorph(name))
        {
            return false;
        }

        this.acquiredMorphs.add(name);

        return true;
    }

    @Override
    public boolean acquiredMorph(String name)
    {
        return this.acquiredMorphs.contains(name);
    }

    @Override
    public List<String> getAcquiredMorphs()
    {
        return acquiredMorphs;
    }

    @Override
    public void setAcquiredMorphs(List<String> morphs)
    {
        this.acquiredMorphs.clear();
        this.acquiredMorphs.addAll(morphs);
    }

    @Override
    public Morph getCurrentMorph()
    {
        return this.morph;
    }

    @Override
    public String getCurrentMorphName()
    {
        return this.name;
    }

    @Override
    public void setCurrentMorph(String name, EntityPlayer player, boolean force)
    {
        if (name.isEmpty())
        {
            this.demorph(player);

            return;
        }

        boolean creative = player != null ? player.isCreative() : false;

        if (force || creative || this.acquiredMorphs.contains(name))
        {
            if (player != null && this.morph != null)
            {
                this.morph.demorph(player);
            }

            this.morph = MorphManager.INSTANCE.morphs.get(name);
            this.name = name;

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
        this.name = "";
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
        this.setCurrentMorph(morphing.getCurrentMorphName(), player, true);
    }
}