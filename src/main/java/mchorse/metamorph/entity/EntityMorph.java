package mchorse.metamorph.entity;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * Entity morph
 * 
 * This entity is responsible for showing up the morph which will player 
 * acquire.
 * 
 * This entity is similar to {@link EntityXPOrb} or {@link EntityItem}, in terms 
 * of picking up.
 */
public class EntityMorph extends EntityLiving implements IEntityAdditionalSpawnData, IMorphProvider
{
    private UUID owner;
    private EntityPlayer player;

    public int timer = 30;
    public AbstractMorph morph;

    /**
     * Initiate the morph and make this entity invulnerable
     */
    public EntityMorph(World worldIn)
    {
        super(worldIn);

        this.setEntityInvulnerable(true);
        this.setCustomNameTag("Morph");
    }

    /**
     * Initiate the morph with morph and owner's UUID 
     */
    public EntityMorph(World worldIn, UUID owner, AbstractMorph morph)
    {
        this(worldIn);

        this.owner = owner;
        this.morph = morph;

        this.setSize(morph);
    }

    @Override
    public AbstractMorph getMorph()
    {
        return this.morph;
    }

    /**
     * Get display name
     */
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation("entity." + this.morph.name + ".name");
    }

    /**
     * Set size based on the morph's characteristics
     */
    private void setSize(AbstractMorph morph)
    {
        this.setSize(MathHelper.clamp_float(morph.getWidth(this), 0, 1.5F), MathHelper.clamp_float(morph.getHeight(this), 0, 2.0F));
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
            if (this.getEntityBoundingBox().intersectsWith(this.player.getEntityBoundingBox()))
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

        if (MorphAPI.acquire(this.player, this.morph))
        {
            this.worldObj.playSound(this.player, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 1.0F, 1.0F);
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

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();

            this.morph.toNBT(tag);
            compound.setTag("Morph", tag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        String owner = compound.getString("Owner");

        this.owner = owner.isEmpty() ? null : UUID.fromString(owner);

        if (compound.hasKey("Morph", 10))
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(compound.getCompoundTag("Morph"));
        }

        this.setSize(morph);
    }

    /* Spawn data read / write */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.owner != null ? this.owner.toString() : "");

        NBTTagCompound tag = new NBTTagCompound();
        this.morph.toNBT(tag);

        boolean hasData = tag != null && !tag.hasNoTags();

        buffer.writeBoolean(hasData);

        if (hasData)
        {
            ByteBufUtils.writeTag(buffer, tag);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        String owner = ByteBufUtils.readUTF8String(buffer);

        this.owner = owner.isEmpty() ? null : UUID.fromString(owner);

        if (buffer.readBoolean())
        {
            NBTTagCompound tag = ByteBufUtils.readTag(buffer);

            this.morph = MorphManager.INSTANCE.morphFromNBT(tag);
        }

        this.setSize(morph);
    }
}