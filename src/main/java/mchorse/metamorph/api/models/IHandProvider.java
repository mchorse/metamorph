package mchorse.metamorph.api.models;

import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * Hand provider interface
 * 
 * This interface is for third-party mods who want to provide first-person 
 * hands for their models.
 * 
 * Implement this interface on any {@link ModelBase} derived classes. The 
 * function of these methods are self explanatory, but if you want to get 
 * the idea what they're doing, see {@link EntityMorph#setupHands()} and 
 * {@link EntityMorph#renderHand(net.minecraft.entity.player.EntityPlayer, net.minecraft.util.EnumHand)} 
 * methods. 
 */
public interface IHandProvider
{
    public ModelRenderer getLeft();

    public ModelRenderer getRight();
}