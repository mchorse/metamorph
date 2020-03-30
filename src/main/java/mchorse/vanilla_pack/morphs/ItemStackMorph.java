package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.item.ItemStack;

public abstract class ItemStackMorph extends AbstractMorph
{
	public abstract void setStack(ItemStack stack);

	public abstract ItemStack getStack();
}