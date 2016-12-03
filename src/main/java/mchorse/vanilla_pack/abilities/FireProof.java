package mchorse.vanilla_pack.abilities;

import net.minecraft.init.MobEffects;

/**
 * Fire proof ability
 * 
 * This abilitiy grants you fire immunity. So basically you're fire proof.
 */
public class FireProof extends PotionAbility
{
    public FireProof()
    {
        this.potion = MobEffects.FIRE_RESISTANCE;
    }
}