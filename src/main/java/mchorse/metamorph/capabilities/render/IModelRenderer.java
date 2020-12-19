package mchorse.metamorph.capabilities.render;

import net.minecraft.entity.EntityLivingBase;

public interface IModelRenderer
{
    public void update(EntityLivingBase entity);

    public boolean canRender();

    public boolean render(EntityLivingBase entity, double x, double y, double z, float partialTicks);
}