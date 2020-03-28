package mchorse.metamorph.capabilities.render;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ModelProvider implements ICapabilityProvider
{
    @CapabilityInject(IModelRenderer.class)
    public static final Capability<IModelRenderer> MODEL = null;

    private IModelRenderer instance = MODEL.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == MODEL;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == MODEL ? MODEL.<T>cast(this.instance) : null;
    }
}