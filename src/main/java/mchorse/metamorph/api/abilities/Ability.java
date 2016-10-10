package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;

public abstract class Ability implements IAbility
{
    @Override
    public void onMorph(EntityPlayer player)
    {}

    @Override
    public void onDemorph(EntityPlayer player)
    {}
}
