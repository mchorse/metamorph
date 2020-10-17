package mchorse.metamorph.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class WorldEventListenerWrapper implements IWorldEventListener
{
    public boolean spawnParticles = true;
    
    protected IWorldEventListener delegate;
    
    public WorldEventListenerWrapper(IWorldEventListener delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        delegate.notifyBlockUpdate(worldIn, pos, oldState, newState, flags);
    }

    @Override
    public void notifyLightSet(BlockPos pos)
    {
        delegate.notifyLightSet(pos);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        delegate.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
            double y, double z, float volume, float pitch)
    {
        delegate.playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos)
    {
        delegate.playRecord(soundIn, pos);
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
            double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        if (spawnParticles)
        {
            delegate.spawnParticle(particleID, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
    }

    @Override
    public void onEntityAdded(Entity entityIn)
    {
        delegate.onEntityAdded(entityIn);
    }

    @Override
    public void onEntityRemoved(Entity entityIn)
    {
        delegate.onEntityRemoved(entityIn);
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        delegate.broadcastSound(soundID, pos, data);
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {
        delegate.playEvent(player, type, blockPosIn, data);
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        delegate.sendBlockBreakProgress(breakerId, pos, progress);
    }

}
