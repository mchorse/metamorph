package mchorse.vanilla_pack.morphs;

import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerMorph extends EntityMorph
{
    public GameProfile profile;

    @Override
    public void setupEntity(World world)
    {
        EntityLivingBase created = null;

        if (world.isRemote)
        {
            created = new PlayerMorphClientEntity(world, this.profile);
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

    @Override
    protected void updateEntity(EntityLivingBase target)
    {
        EntityPlayer entity = (EntityPlayer) this.entity;

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPlayerPreTick(entity);

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

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPlayerPostTick(entity);
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        PlayerMorph morph = new PlayerMorph();

        morph.name = this.name;
        morph.settings = this.settings;
        morph.entityData = this.entityData.copy();
        morph.profile = this.profile;

        return morph;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("PlayerProfile", 10))
        {
            this.profile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag("PlayerProfile"));
        }
        else if (tag.hasKey("PlayerName"))
        {
            this.profile = new GameProfile(null, tag.getString("PlayerName"));
            this.profile = TileEntitySkull.updateGameprofile(this.profile);
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

    public static class PlayerMorphClientEntity extends EntityOtherPlayerMP
    {
        public GameProfile profile;
        public boolean isBaby;

        public PlayerMorphClientEntity(World world, GameProfile profile)
        {
            super(world, profile);

            this.profile = profile;
        }

        @Override
        public boolean isChild()
        {
            return this.isBaby;
        }

        @Override
        public ResourceLocation getLocationSkin()
        {
            ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

            if (profile != null)
            {
                Minecraft minecraft = Minecraft.getMinecraft();
                Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

                if (map.containsKey(Type.SKIN))
                {
                    resourcelocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN);
                }
                else
                {
                    UUID uuid = EntityPlayer.getUUID(profile);
                    resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                }
            }

            return resourcelocation;
        }

        public ResourceLocation getLocationCape()
        {
            ResourceLocation resourcelocation = null;

            if (profile != null)
            {
                Minecraft minecraft = Minecraft.getMinecraft();
                Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

                if (map.containsKey(Type.CAPE))
                {
                    resourcelocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture) map.get(Type.CAPE), Type.CAPE);
                }
            }

            return resourcelocation;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean isWearing(EnumPlayerModelParts part)
        {
            return true;
        }

        public boolean hasPlayerInfo()
        {
            return true;
        }

        @Override
        public void readEntityFromNBT(NBTTagCompound compound)
        {
            super.readEntityFromNBT(compound);

            this.isBaby = compound.getBoolean("IsBaby");
        }

        @Override
        public void writeEntityToNBT(NBTTagCompound compound)
        {
            super.writeEntityToNBT(compound);

            compound.setBoolean("IsBaby", this.isBaby);
        }
    }
}