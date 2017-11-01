package mchorse.metamorph.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AirPossiblyRenderingEvent extends Event
{
    private RenderGameOverlayEvent eventParent;
    private float partialTicks;
    private ScaledResolution resolution;
    
    public AirPossiblyRenderingEvent(RenderGameOverlayEvent eventParent, float partialTicks, ScaledResolution resolution)
    {
        this.eventParent = eventParent;
        this.partialTicks = partialTicks;
        this.resolution = resolution;
    }
    
    public RenderGameOverlayEvent getEventParent()
    {
        return eventParent;
    }
    
    public float getPartialTicks()
    {
        return partialTicks;
    }
    
    public ScaledResolution getResolution()
    {
        return resolution;
    }
    
    public static void hook(RenderGameOverlayEvent eventParent, float partialTicks, ScaledResolution resolution)
    {
        MinecraftForge.EVENT_BUS.post(new AirPossiblyRenderingEvent(eventParent, partialTicks, resolution));
    }
}
