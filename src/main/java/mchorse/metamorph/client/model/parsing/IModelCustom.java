package mchorse.metamorph.client.model.parsing;

import mchorse.metamorph.client.model.ModelCustom;
import mchorse.metamorph.client.model.ModelCustomRenderer;

/**
 * Custom model interface
 * 
 * This interface is used as a trait for models that extend from 
 * {@link ModelCustom} for {@link ModelParser} to inject the 
 * {@link ModelCustomRenderer}s in the public fields of the instance.
 */
public interface IModelCustom
{
    /**
     * Gets called when the {@link ModelParser} has finished generating its 
     * limbs.
     */
    public void onGenerated();
}