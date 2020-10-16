package mchorse.metamorph.api.morphs.utils;

import mchorse.metamorph.api.morphs.AbstractMorph;

public interface ISyncableMorph
{
	public void pause(AbstractMorph previous, int offset);

	public boolean isPaused();
}