package mchorse.metamorph.bodypart;

import com.google.common.base.Objects;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BodyPart implements IBodyPart
{
    public String limb = "";
    public MorphBodyPart part;

    @Override
    @SideOnly(Side.CLIENT)
    public void init()
    {
        if (this.part != null) this.part.init();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, float partialTicks)
    {
        if (this.part != null) this.part.render(entity, partialTicks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update(EntityLivingBase entity, IMorphing cap)
    {
        if (this.part != null) this.part.update(entity, cap);
    }

    @Override
    public boolean canMerge(IBodyPart part, boolean isRemote)
    {
        if (part instanceof BodyPart)
        {
            BodyPart aPart = (BodyPart) part;

            this.limb = aPart.limb;

            if (this.part == null || !this.part.canMerge(aPart.part, isRemote))
            {
                this.part = aPart.part == null ? null : aPart.part.clone(isRemote);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof BodyPart)
        {
            BodyPart part = (BodyPart) obj;

            result = result && Objects.equal(this.limb, part.limb);
            result = result && Objects.equal(this.part, part.part);
        }

        return result;
    }

    public BodyPart clone(boolean isRemote)
    {
        BodyPart part = new BodyPart();

        part.limb = this.limb;
        part.part = this.part.clone(isRemote);

        return part;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        if (this.part == null)
        {
            return;
        }

        if (!this.limb.isEmpty()) tag.setString("Limb", this.limb);
        this.part.toNBT(tag);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.limb = tag.getString("Limb");
        this.part = new MorphBodyPart();
        this.part.fromNBT(tag);
    }
}