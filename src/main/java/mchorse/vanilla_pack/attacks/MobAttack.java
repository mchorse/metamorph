package mchorse.vanilla_pack.attacks;

import mchorse.metamorph.api.abilities.IAttackAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.entity.player.EntityPlayer;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;

/**
 * Mob attack ability
 * 
 * This ability uses the mob's attack when the player attacks.
 * Both attacks will go through, taking the highest damage between
 * the player and the mob, and triggering any special effects the
 * mob does on attack.
 */
public class MobAttack implements IAttackAbility
{
    @Override
    public void attack(Entity target, EntityLivingBase source)
    {
        if (source instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) source;
			IMorphing capability = Morphing.get(player);
			if(capability == null)
				return;
			AbstractMorph currentMorph = capability.getCurrentMorph();
			if(currentMorph == null)
				return;
			if(currentMorph instanceof EntityMorph)
			{
				EntityMorph currentEntityMorph = (EntityMorph) currentMorph;
				currentEntityMorph.getEntity(source.world).attackEntityAsMob(target);
			}
        }
    }
}
