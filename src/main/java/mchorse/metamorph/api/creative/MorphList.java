package mchorse.metamorph.api.creative;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Morph list
 */
public class MorphList
{
    /**
     * Morph sections
     */
    public List<MorphSection> sections = new ArrayList<MorphSection>();

    /**
     * Add a morph section to this list
     */
    public void register(MorphSection section)
    {
        this.sections.add(section);
    }

    /**
     * This method gets called when a new morph picker appears
     */
    public void update(World world)
    {
        for (MorphSection section : this.sections)
        {
            section.update(world);
        }
    }

    /**
     * This method gets called when player exits to the main menu
     */
    public void reset()
    {
        for (MorphSection section : this.sections)
        {
            section.reset();
        }
    }
}