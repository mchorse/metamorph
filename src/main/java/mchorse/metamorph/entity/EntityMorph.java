package mchorse.metamorph.entity;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * Entity morph
 * 
 * This entity is responsible for showing up the morph which will player 
 * acquire.
 * 
 * This entity is similar to {@link EntityXPOrb} or {@link EntityItem}
 */
public class EntityMorph extends EntityLiving implements IEntityAdditionalSpawnData
{
    private UUID owner;
    private EntityPlayer player;

    public int timer = 30;
    public String morph = "";

    /**
     * Initiate the morph and make this entity invulnerable
     */
    public EntityMorph(World worldIn)
    {
        super(worldIn);

        this.setEntityInvulnerable(true);
    }

    public EntityMorph(World worldIn, UUID owner, String morph)
    {
        this(worldIn);

        this.owner = owner;
        this.morph = morph;

        this.setCustomNameTag(morph + " Morph");
    }

    /**
     * No! Don't despawn morphs, they're like currency! 
     */
    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * Update method
     * 
     * This method is responsible for looking for player owner and advancing 
     * itself toward the player. It will also die and grant the morph to the 
     * player when it will be very close to player.
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.timer > 0)
        {
            this.timer--;

            return;
        }

        if ((this.player == null || this.player.isDead) && !this.worldObj.isRemote && this.owner != null)
        {
            this.player = this.worldObj.getPlayerEntityByUUID(this.owner);
        }

        if (this.player != null && !this.player.isDead)
        {
            double dx = this.posX - this.player.posX;
            double dy = this.posY - this.player.posY;
            double dz = this.posZ - this.player.posZ;

            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist < this.player.width * 1.25)
            {
                this.setDead();
                this.grantMorph();
            }
        }
    }

    /**
     * Grant morph to the player
     * 
     * This method is responsible for giving this morph to the player. 
     */
    private void grantMorph()
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        IMorphing capability = this.player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability.acquireMorph(morph))
        {
            Dispatcher.sendTo(new PacketAcquireMorph(morph), (EntityPlayerMP) player);

            player.addChatMessage(new TextComponentString("You gained §o§7" + morph + "§r morph!"));
        }
    }

    /**
     * Advance to current player
     * 
     * This method basically turn morph entity in player's side and moves 
     * toward player.
     */
    private void advanceToPlayer(double dx, double dy, double dz, double dist)
    {
        final double speed = 0.1;

        this.rotationYaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) + 90.0F;
        this.rotationPitch = (float) (MathHelper.atan2(dy, dist) * (180D / Math.PI));

        if (Math.abs(dx) > speed * 4)
        {
            this.motionX = Math.copySign(speed, -dx);
        }

        this.motionY = Math.copySign(speed, -dy);

        if (Math.abs(dz) > speed * 4)
        {
            this.motionZ = Math.copySign(speed, -dz);
        }
    }

    /* Read / write */

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        if (this.owner != null)
        {
            compound.setString("Owner", this.owner.toString());
        }

        if (!this.morph.isEmpty())
        {
            compound.setString("Morph", this.morph);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        String owner = compound.getString("Owner");

        this.owner = owner.isEmpty() ? null : UUID.fromString(owner);
        this.morph = compound.getString("Morph");
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.owner != null ? this.owner.toString() : "");
        ByteBufUtils.writeUTF8String(buffer, this.morph);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        String owner = ByteBufUtils.readUTF8String(buffer);

        this.owner = owner.isEmpty() ? null : UUID.fromString(owner);
        this.morph = ByteBufUtils.readUTF8String(buffer);

        this.setCustomNameTag(morph);
    }
}