package mchorse.metamorph.capabilities.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelRenderer
{
    public void update(EntityLivingBase entity);

    @SideOnly(Side.CLIENT)
    public void updateSelector(EntityLivingBase entity);

    public boolean canRender();

    public boolean render(EntityLivingBase entity, double x, double y, double z, float partialTicks);
}