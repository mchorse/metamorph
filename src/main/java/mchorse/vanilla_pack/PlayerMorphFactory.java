package mchorse.vanilla_pack;

import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Player morph factory
 * 
 * This morph factory allows making a morph using player's username. 
 * Basically player's disguising.
 */
public class PlayerMorphFactory implements IMorphFactory
{
    @Override
    public void register(MorphManager manager)
    {}

    @Override
    public void registerClient(MorphManager manager)
    {}

    /**
     * Return game profile's username as for player's name 
     */
    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morph)
    {
        if (morph instanceof PlayerMorph)
        {
            return ((PlayerMorph) morph).profile.getName();
        }

        return null;
    }

    @Override
    public void getMorphs(MorphList morphs, World world)
    {}

    @Override
    public boolean hasMorph(String name)
    {
        return name.equals("Player");
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        if (tag.getString("Name").equals("Player"))
        {
            PlayerMorph player = new PlayerMorph();

            player.fromNBT(tag);

            return player;
        }

        return null;
    }
}