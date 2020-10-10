package mchorse.metamorph.capabilities.render;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ModelRendererStorage implements IStorage<IModelRenderer>
{
    @Override
    public NBTBase writeNBT(Capability<IModelRenderer> capability, IModelRenderer instance, EnumFacing side)
    {
        return new NBTTagCompound();
    }

    @Override
    public void readNBT(Capability<IModelRenderer> capability, IModelRenderer instance, EnumFacing side, NBTBase nbt)
    {}
}