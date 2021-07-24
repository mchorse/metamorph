package mchorse.metamorph.api.morphs.utils;

import mchorse.metamorph.api.morphs.AbstractMorph;

public interface IMorphGenerator
{
    public boolean canGenerate();

    public AbstractMorph genCurrentMorph(float partialTicks);
}
