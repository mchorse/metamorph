package mchorse.metamorph.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Reload morphs event
 * 
 * This event gets fired when creative morphs picker gets initiated
 */
@SideOnly(Side.CLIENT)
public class ReloadMorphs extends Event
{
    public ReloadMorphs()
    {}
}