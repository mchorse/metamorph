package mchorse.metamorph.network.common;

import mchorse.metamorph.api.morphs.AbstractMorph;

/**
 * Acquire morph packet
 */
public class PacketAcquireMorph extends PacketMorph
{
    public PacketAcquireMorph()
    {
        super();
    }

    public PacketAcquireMorph(AbstractMorph morph)
    {
        super(morph);
    }
}