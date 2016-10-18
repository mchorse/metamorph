package mchorse.metamorph.api.actions;

import mchorse.metamorph.api.IAction;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Potions action
 * 
 * This action is responsible for throwing a splash potion in the direction 
 * where player looks.
 * 
 * This action may throw instant harm, slowness, weakness or poison splash 
 * potions depending on randomness, but most of the time it'll be instant harm 
 * potion.
 * 
 * Of course, some of the code of this action was taken fron 
 * {@link EntityWitch}.
 */
public class Potions implements IAction
{
    @Override
    public void execute(EntityPlayer player)
    {
        World world = player.worldObj;

        if (world.isRemote)
        {
            return;
        }

        Vec3d look = player.getLook(1.0F);
        PotionType effect = PotionTypes.HARMING;

        if (player.getRNG().nextFloat() < 0.2)
        {
            effect = PotionTypes.SLOWNESS;
        }
        else if (player.getRNG().nextFloat() < 0.1)
        {
            effect = PotionTypes.WEAKNESS;
        }
        else if (player.getRNG().nextFloat() < 0.05)
        {
            effect = PotionTypes.POISON;
        }

        ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), effect);
        EntityPotion potion = new EntityPotion(world, player, stack);

        potion.rotationPitch += 20.0F;
        potion.setThrowableHeading(look.xCoord, look.yCoord, look.zCoord, 0.75F, 8.0F);

        world.spawnEntityInWorld(potion);
    }
}