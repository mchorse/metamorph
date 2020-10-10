package mchorse.vanilla_pack.morphs;

import com.mojang.authlib.GameProfile;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Player morph
 * 
 * This morph basically does nothing than providing some code for 
 * bootstrapping a player isolated in this morph.
 */
public class PlayerMorph extends EntityMorph
{
    /**
     * Player's profile 
     */
    public GameProfile profile;

    public PlayerMorph()
    {
        this.name = "player";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        return this.profile == null ? super.getSubclassDisplayName() : this.profile.getName();
    }

    public void setProfile(String username)
    {
        this.profile = new GameProfile(null, username);
        this.profile = TileEntitySkull.updateGameprofile(this.profile);
    }

    /**
     * Create a player morph 
     */
    @Override
    public void setupEntity(World world)
    {
        EntityLivingBase created = null;

        if (world.isRemote)
        {
            created = this.getPlayerClient(world);
        }
        else
        {
            created = new EntityPlayer(world, this.profile)
            {
                @Override
                public boolean isSpectator()
                {
                    return true;
                }

                @Override
                public boolean isCreative()
                {
                    return false;
                }
            };
        }

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

    /**
     * Encapsulate the code into removable (on client side) method 
     */
    @SideOnly(Side.CLIENT)
    private EntityPlayer getPlayerClient(World world)
    {
        return new PlayerMorphClientEntity(world, this.profile);
    }

    /**
     * Updates the player entity, but not using its update methods, but 
     * rather some code that only updates player's cape and some other 
     * stuff.
     */
    @Override
    protected void updateEntity(EntityLivingBase target)
    {
        EntityPlayer entity = (EntityPlayer) this.entity;
        EnumHandSide hand = target.getPrimaryHand();

        hand = hand == EnumHandSide.LEFT ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
        entity.setPrimaryHand(hand);

        /* Update the cape */
        entity.prevChasingPosX = entity.chasingPosX;
        entity.prevChasingPosY = entity.chasingPosY;
        entity.prevChasingPosZ = entity.chasingPosZ;
        double d0 = entity.posX - entity.chasingPosX;
        double d1 = entity.posY - entity.chasingPosY;
        double d2 = entity.posZ - entity.chasingPosZ;

        if (d0 > 10.0D)
        {
            entity.chasingPosX = entity.posX;
            entity.prevChasingPosX = entity.chasingPosX;
        }

        if (d2 > 10.0D)
        {
            entity.chasingPosZ = entity.posZ;
            entity.prevChasingPosZ = entity.chasingPosZ;
        }

        if (d1 > 10.0D)
        {
            entity.chasingPosY = entity.posY;
            entity.prevChasingPosY = entity.chasingPosY;
        }

        if (d0 < -10.0D)
        {
            entity.chasingPosX = entity.posX;
            entity.prevChasingPosX = entity.chasingPosX;
        }

        if (d2 < -10.0D)
        {
            entity.chasingPosZ = entity.posZ;
            entity.prevChasingPosZ = entity.chasingPosZ;
        }

        if (d1 < -10.0D)
        {
            entity.chasingPosY = entity.posY;
            entity.prevChasingPosY = entity.chasingPosY;
        }

        entity.chasingPosX += d0 * 0.25D;
        entity.chasingPosZ += d2 * 0.25D;
        entity.chasingPosY += d1 * 0.25D;
    }

    @Override
    public AbstractMorph create()
    {
        return new PlayerMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof PlayerMorph)
        {
            PlayerMorph morph = (PlayerMorph) from;

            this.profile = morph.profile;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof PlayerMorph)
        {
            PlayerMorph morph = (PlayerMorph) obj;

            result = result && morph.profile.equals(this.profile);
        }

        return result;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.profile = null;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("PlayerProfile", 10))
        {
            this.profile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag("PlayerProfile"));
        }
        else if (tag.hasKey("Username"))
        {
            this.setProfile(tag.getString("Username"));
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.profile != null)
        {
            NBTTagCompound profileTag = new NBTTagCompound();

            NBTUtil.writeGameProfile(profileTag, this.profile);
            tag.setTag("PlayerProfile", profileTag);
        }
    }

    /**
     * Placeholder player morph entity
     * 
     * This player entity is used for overriding some of its methods to 
     * provide stable functionality of player morph in its isolated 
     * environment.
     */
    @SideOnly(Side.CLIENT)
    public static class PlayerMorphClientEntity extends EntityOtherPlayerMP
    {
        public GameProfile profile;
        public boolean isBaby;
        public String skinType = "";

        /**
         * Player's network info
         */
        public NetworkPlayerInfo info;

        public PlayerMorphClientEntity(World world, GameProfile profile)
        {
            super(world, profile);

            this.profile = profile;
        }

        /**
         * Initiate network info property thing 
         */
        protected void initiateNetworkInfo()
        {
            if (this.info == null)
            {
                this.info = new NetworkPlayerInfo(this.profile);
            }
        }

        @Override
        public boolean isChild()
        {
            return this.isBaby;
        }

        @Override
        public String getSkinType()
        {
            if (this.skinType.isEmpty())
            {
                this.initiateNetworkInfo();
                return this.info.getSkinType();
            }

            return this.skinType.equals("alex") ? "slim" : "default";
        }

        /**
         * Get this player's skin 
         */
        @Override
        public ResourceLocation getLocationSkin()
        {
            this.initiateNetworkInfo();
            return this.info.getLocationSkin();
        }

        /**
         * Get this player's custom cape skin 
         */
        @Override
        public ResourceLocation getLocationCape()
        {
            this.initiateNetworkInfo();
            return this.info.getLocationCape();
        }

        /**
         * This player is always wearing every part of the body 
         */
        @Override
        @SideOnly(Side.CLIENT)
        public boolean isWearing(EnumPlayerModelParts part)
        {
            return true;
        }

        @Override
        public boolean hasPlayerInfo()
        {
            return this.info != null;
        }

        @Override
        public void readEntityFromNBT(NBTTagCompound compound)
        {
            super.readEntityFromNBT(compound);

            this.isBaby = compound.getBoolean("IsBaby");
            this.skinType = compound.getString("SkinType");
        }

        @Override
        public void writeEntityToNBT(NBTTagCompound compound)
        {
            super.writeEntityToNBT(compound);

            compound.setBoolean("IsBaby", this.isBaby);
            compound.setString("SkinType", this.skinType);
        }
    }
}