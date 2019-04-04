package mchorse.metamorph.bodypart;

import org.lwjgl.opengl.GL11;

import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Morph body part
 */
public class MorphBodyPart implements IBodyPart
{
    public AbstractMorph morph;
    public ItemStack[] slots = new ItemStack[6];
    public float[] translate = new float[3];
    public float[] scale = new float[] {1, 1, 1};
    public float[] rotate = new float[] {180F, 0F, 0F};
    public boolean useTarget = false;

    @SideOnly(Side.CLIENT)
    private EntityLivingBase entity;

    @Override
    @SideOnly(Side.CLIENT)
    public void init()
    {
        this.entity = new DummyEntity(Minecraft.getMinecraft().theWorld);
        this.entity.rotationYaw = this.entity.prevRotationYaw;
        this.entity.rotationYawHead = this.entity.prevRotationYawHead;
        this.entity.onGround = true;

        this.updateEntity();
    }

    @SideOnly(Side.CLIENT)
    public void updateEntity()
    {
        if (this.entity == null)
        {
            return;
        }

        for (int i = 0; i < this.slots.length; i++)
        {
            this.entity.setItemStackToSlot(EntityEquipmentSlot.values()[i], this.slots[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, float partialTicks)
    {
        entity = this.useTarget ? entity : this.entity;

        if (this.morph == null || entity == null)
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.translate[0], this.translate[1], this.translate[2]);

        GL11.glRotatef(this.rotate[2], 0, 0, 1);
        GL11.glRotatef(this.rotate[1], 0, 1, 0);
        GL11.glRotatef(this.rotate[0], 1, 0, 0);

        GL11.glScalef(this.scale[0], this.scale[1], this.scale[2]);

        float rotationYaw = entity.renderYawOffset;
        float prevRotationYaw = entity.prevRenderYawOffset;
        float rotationYawHead = entity.rotationYawHead;
        float prevRotationYawHead = entity.prevRotationYawHead;

        entity.rotationYawHead = entity.rotationYawHead - entity.renderYawOffset;
        entity.prevRotationYawHead = entity.prevRotationYawHead - entity.prevRenderYawOffset;
        entity.renderYawOffset = entity.prevRenderYawOffset = 0;

        this.morph.render(entity, 0, 0, 0, 0, partialTicks);

        entity.renderYawOffset = rotationYaw;
        entity.prevRenderYawOffset = prevRotationYaw;
        entity.rotationYawHead = rotationYawHead;
        entity.prevRotationYawHead = prevRotationYawHead;

        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update(EntityLivingBase entity, IMorphing cap)
    {
        entity = this.useTarget ? entity : this.entity;

        if (entity != null)
        {
            if (!this.useTarget)
            {
                this.entity.ticksExisted++;
            }

            if (this.morph != null)
            {
                this.morph.update(entity, cap);
            }
        }
    }

    @Override
    public boolean canMerge(IBodyPart part, boolean isRemote)
    {
        if (part instanceof MorphBodyPart)
        {
            MorphBodyPart morph = (MorphBodyPart) part;

            if (this.morph == null || !this.morph.canMerge(morph.morph, isRemote))
            {
                this.morph = morph.morph.clone(isRemote);
            }
        }

        return false;
    }

    public MorphBodyPart clone(boolean isRemote)
    {
        MorphBodyPart part = new MorphBodyPart();

        part.morph = this.morph == null ? null : this.morph.clone(isRemote);
        part.translate[0] = this.translate[0];
        part.translate[1] = this.translate[1];
        part.translate[2] = this.translate[2];
        part.scale[0] = this.scale[0];
        part.scale[1] = this.scale[1];
        part.scale[2] = this.scale[2];
        part.rotate[0] = this.rotate[0];
        part.rotate[1] = this.rotate[1];
        part.rotate[2] = this.rotate[2];
        part.useTarget = this.useTarget;

        for (int i = 0; i < this.slots.length; i++)
        {
            part.slots[i] = this.slots[i];
        }

        return part;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Morph", 10))
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph"));
        }

        if (tag.hasKey("Items", 9))
        {
            NBTTagList items = tag.getTagList("Items", 10);

            for (int i = 0, c = items.tagCount(); i < c; i++)
            {
                NBTTagCompound compound = items.getCompoundTagAt(i);
                ItemStack stack = ItemStack.loadItemStackFromNBT(compound);

                this.slots[i] = stack;
            }
        }

        NBTUtils.readFloatList(tag.getTagList("T", 5), this.translate);
        NBTUtils.readFloatList(tag.getTagList("S", 5), this.scale);
        NBTUtils.readFloatList(tag.getTagList("R", 5), this.rotate);

        if (tag.hasKey("Target")) this.useTarget = tag.getBoolean("Target");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        if (this.morph != null)
        {
            NBTTagCompound morph = new NBTTagCompound();

            this.morph.toNBT(morph);
            tag.setTag("Morph", morph);
        }

        NBTTagList list = new NBTTagList();

        for (int i = 0; i < this.slots.length; i++)
        {
            NBTTagCompound compound = new NBTTagCompound();
            ItemStack stack = this.slots[i];

            if (stack != null)
            {
                stack.writeToNBT(compound);
            }

            list.appendTag(compound);
        }

        tag.setTag("Items", list);

        if (this.translate[0] != 0 || this.translate[1] != 0 || this.translate[2] != 0)
        {
            tag.setTag("T", NBTUtils.writeFloatList(new NBTTagList(), this.translate));
        }

        if (this.scale[0] != 0 || this.scale[1] != 0 || this.scale[2] != 0)
        {
            tag.setTag("S", NBTUtils.writeFloatList(new NBTTagList(), this.scale));
        }

        if (this.rotate[0] != 180 || this.rotate[1] != 0 || this.rotate[2] != 0)
        {
            tag.setTag("R", NBTUtils.writeFloatList(new NBTTagList(), this.rotate));
        }

        if (this.useTarget) tag.setBoolean("Target", this.useTarget);
    }
}