package mchorse.metamorph.api.abilities;

import java.util.Random;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

/**
 * Sun allergy ability 
 * 
 * This abilitiy does cool stuff. It sets player on fire when he's on the sun. 
 * It will be used by the coolest mobs in the game skeleton and zombie.
 * 
 * This is more like a disability than an ability *Ba-dum-pam-dum-tsss*
 */
public class SunAllergy implements IAbility
{
    private MutableBlockPos pos = new MutableBlockPos(0, 0, 0);
    private Random random = new Random();

    @Override
    public void update(EntityPlayer player)
    {
        if (!player.worldObj.isDaytime() || player.worldObj.isRemote)
        {
            return;
        }

        float brightness = player.getBrightness(1.0F);
        boolean doit = this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F;
        this.pos.setPos(player.posX, player.posY, player.posZ);

        if (brightness > 0.5 && doit && player.worldObj.canSeeSky(pos))
        {
            player.setFire(8);
        }
    }
}