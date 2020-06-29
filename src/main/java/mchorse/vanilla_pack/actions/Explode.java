package mchorse.vanilla_pack.actions;

import javax.annotation.Nullable;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Explode action
 * 
 * This action makes an explosion and also kills the player. Why kill also the 
 * player? Because it won't be so creeper if he won't die. 
 * 
 * EXPLOSIONS!!! Mr. Torgue approves this action. 
 */
public class Explode implements IAction
{
    @Override
    public void execute(EntityLivingBase target, @Nullable AbstractMorph morph)
    {
        if (target.world.isRemote)
        {
            return;
        }
		
		int explosionPower = 3;
		boolean isPowered = false;
		
        if (morph instanceof EntityMorph)
        {
            EntityLivingBase entity = ((EntityMorph) morph).getEntity();

            if (entity instanceof EntityCreeper)
            {
                explosionPower = ReflectionHelper.getPrivateValue(EntityCreeper.class, (EntityCreeper) entity, "explosionRadius", "field_82226_g");
				isPowered = ((EntityCreeper) entity).getPowered();
            }
        }

		float f = isPowered ? 2.0F : 1.0F;
        target.world.createExplosion(target, target.posX, target.posY, target.posZ, explosionPower * f, true);

        if (!(target instanceof EntityPlayer) || (target instanceof EntityPlayer && !((EntityPlayer) target).isCreative()))
        {
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, target.getMaxHealth());
        }
    }
}