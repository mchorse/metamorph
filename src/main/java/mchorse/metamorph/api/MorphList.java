package mchorse.metamorph.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.GuiMenu;
import mchorse.metamorph.client.gui.GuiMorphs;

/**
 * Morph list
 * 
 * This class is going to be used in {@link GuiMenu} and in {@link GuiMorphs} 
 * for displaying morphs in GUIs. It's generated in {@link MorphManager}. 
 */
public class MorphList
{
    /**
     * This field is going to be responsible storing morphs. Feel free to use 
     * this field directly, but don't abuse this privilege.  
     */
    public Map<String, List<AbstractMorph>> morphs = new HashMap<String, List<AbstractMorph>>();

    /**
     * Checks if this list has a morph by given name 
     */
    public boolean hasMorph(String name)
    {
        return this.morphs.containsKey(name);
    }

    /**
     * Add a morph to this morph list. 
     * 
     * If this list already has a morph by this name, the operation is 
     * cancelled.  
     */
    public void addMorph(String name, AbstractMorph morph)
    {
        if (this.hasMorph(name))
        {
            return;
        }

        List<AbstractMorph> list = new ArrayList<AbstractMorph>();

        list.add(morph);
        this.morphs.put(name, list);
    }

    /**
     * Add a morph variant to this morph list.
     * 
     * This is like {@link #addMorph(String, AbstractMorph)}, but it appends 
     * another morph. Basically, it adds a morph variant.
     */
    public void addMorphVariant(String name, AbstractMorph morph)
    {
        if (this.hasMorph(name))
        {
            this.morphs.get(name).add(morph);
        }
        else
        {
            this.addMorph(name, morph);
        }
    }
}