package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.World;

public class DragonMorph extends EntityMorph {
	public static class EntityDragonMorph extends EntityDragon
	{
		public EntityDragonMorph(World p_i1700_1_) {
			super(p_i1700_1_);
		}
		private int ownerid;
		public void setOwner(EntityLivingBase o)
		{
			ownerid = o.getEntityId();
		}
		public boolean isOwner(EntityLivingBase e)
		{
			return e.getEntityId() == ownerid;
		}
	}

	public void setupEntity(World world)
	{
		 EntityDragonMorph created = new EntityDragonMorph(world);
	     created.deserializeNBT(this.entityData);
	     created.deathTime = 0;
	     created.hurtTime = 0;
	     created.limbSwing = 0;
	     created.setFire(0);

	     this.setEntity(created);

	     if (world.isRemote)
	     {
	         this.setupRenderer();
	     }
	}
	protected void updateSize(EntityLivingBase target, float width, float height)
    {
        super.updateSize(target, width, height);
        ((EntityDragonMorph)(this.entity)).setOwner(target);
    }
}
