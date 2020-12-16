package mchorse.metamorph.bodypart;

import com.google.common.base.Objects;
import mchorse.mclib.client.Draw;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.metamorph.api.MorphUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.Morph;
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

import javax.vecmath.Vector3f;

/**
 * Morph body part
 */
public class BodyPart
{
    public Morph morph = new Morph();
    public ItemStack[] slots = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    public Vector3f translate = new Vector3f();
    public Vector3f scale = new Vector3f(1, 1, 1);
    public Vector3f rotate = new Vector3f(180, 0, 0);
    public boolean useTarget = false;
    public boolean enabled = true;
    public String limb = "";

    private EntityLivingBase entity;

    @SideOnly(Side.CLIENT)
    public void init()
    {
        this.entity = new DummyEntity(Minecraft.getMinecraft().world);
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

    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, float partialTicks)
    {
        entity = this.useTarget ? entity : this.entity;

        if (this.morph.get() == null || entity == null || !this.enabled)
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.translate.x, this.translate.y, this.translate.z);

        GL11.glRotatef(this.rotate.z, 0, 0, 1);
        GL11.glRotatef(this.rotate.y, 0, 1, 0);
        GL11.glRotatef(this.rotate.x, 1, 0, 0);

        GL11.glScalef(this.scale.x, this.scale.y, this.scale.z);

        float rotationYaw = entity.renderYawOffset;
        float prevRotationYaw = entity.prevRenderYawOffset;
        float rotationYawHead = entity.rotationYawHead;
        float prevRotationYawHead = entity.prevRotationYawHead;

        entity.rotationYawHead = entity.rotationYawHead - entity.renderYawOffset;
        entity.prevRotationYawHead = entity.prevRotationYawHead - entity.prevRenderYawOffset;
        entity.renderYawOffset = entity.prevRenderYawOffset = 0;

        MorphUtils.render(this.morph.get(), entity, 0, 0, 0, 0, partialTicks);

        entity.renderYawOffset = rotationYaw;
        entity.prevRenderYawOffset = prevRotationYaw;
        entity.rotationYawHead = rotationYawHead;
        entity.prevRotationYawHead = prevRotationYawHead;

        /* Draw axis point for body part renderer */
        if (GuiModelRenderer.isRendering())
        {
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.disableTexture2D();

            GlStateManager.disableDepth();
            GlStateManager.disableLighting();

            Draw.point(0, 0, 0);

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
        }

        GL11.glPopMatrix();
    }

    public void update(EntityLivingBase entity)
    {
        entity = this.useTarget ? entity : this.entity;

        if (entity != null && this.enabled)
        {
            if (!this.useTarget)
            {
                this.entity.ticksExisted++;
            }

            AbstractMorph morph = this.morph.get();

            if (morph != null)
            {
                float rotationYaw = entity.renderYawOffset;
                float prevRotationYaw = entity.prevRenderYawOffset;
                float rotationYawHead = entity.rotationYawHead;
                float prevRotationYawHead = entity.prevRotationYawHead;

                entity.rotationYawHead = entity.rotationYawHead - entity.renderYawOffset;
                entity.prevRotationYawHead = entity.prevRotationYawHead - entity.prevRenderYawOffset;
                entity.renderYawOffset = entity.prevRenderYawOffset = 0;

                morph.update(entity);

                entity.renderYawOffset = rotationYaw;
                entity.prevRenderYawOffset = prevRotationYaw;
                entity.rotationYawHead = rotationYawHead;
                entity.prevRotationYawHead = prevRotationYawHead;
            }
        }
    }

    public boolean canMerge(BodyPart part)
    {
        this.morph.copy(part.morph);
        this.translate.set(part.translate);
        this.scale.set(part.scale);
        this.rotate.set(part.rotate);
        this.useTarget = part.useTarget;
        this.enabled = part.enabled;

        for (int i = 0; i < part.slots.length; i++)
        {
            this.slots[i] = part.slots[i];
        }

        this.limb = part.limb;

        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof BodyPart)
        {
            BodyPart part = (BodyPart) obj;

            result = Objects.equal(this.morph, part.morph);
            result = result && Objects.equal(this.translate, part.translate);
            result = result && Objects.equal(this.scale, part.scale);
            result = result && Objects.equal(this.rotate, part.rotate);
            result = result && this.useTarget == part.useTarget;
            result = result && this.enabled == part.enabled;

            for (int i = 0; i < this.slots.length; i++)
            {
                result = result && ItemStack.areItemStacksEqual(this.slots[i], part.slots[i]);
            }

            result = result && Objects.equal(this.limb, part.limb);
        }

        return result;
    }

    public BodyPart copy()
    {
        BodyPart part = new BodyPart();

        part.morph.copy(this.morph);
        part.translate.set(this.translate);
        part.scale.set(this.scale);
        part.rotate.set(this.rotate);
        part.useTarget = this.useTarget;
        part.enabled = this.enabled;

        for (int i = 0; i < this.slots.length; i++)
        {
            part.slots[i] = this.slots[i];
        }

        part.limb = this.limb;

        return part;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Morph", 10))
        {
            this.morph.fromNBT(tag.getCompoundTag("Morph"));
        }

        if (tag.hasKey("Items", 9))
        {
            NBTTagList items = tag.getTagList("Items", 10);

            for (int i = 0, c = items.tagCount(); i < c; i++)
            {
                NBTTagCompound compound = items.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(compound);

                this.slots[i] = stack;
            }
        }

        NBTUtils.readFloatList(tag.getTagList("T", 5), this.translate);
        NBTUtils.readFloatList(tag.getTagList("S", 5), this.scale);
        NBTUtils.readFloatList(tag.getTagList("R", 5), this.rotate);

        if (tag.hasKey("Target")) this.useTarget = tag.getBoolean("Target");
        if (tag.hasKey("Enabled")) this.enabled = tag.getBoolean("Enabled");
        if (tag.hasKey("Limb")) this.limb = tag.getString("Limb");
    }

    public void toNBT(NBTTagCompound tag)
    {
        NBTTagCompound morph = this.morph.toNBT();

        if (morph != null)
        {
            tag.setTag("Morph", morph);
        }

        NBTTagList list = new NBTTagList();

        for (int i = 0; i < this.slots.length; i++)
        {
            NBTTagCompound compound = new NBTTagCompound();
            ItemStack stack = this.slots[i];

            if (!stack.isEmpty())
            {
                stack.writeToNBT(compound);
            }

            list.appendTag(compound);
        }

        tag.setTag("Items", list);

        if (this.translate.x != 0 || this.translate.y != 0 || this.translate.z != 0)
        {
            tag.setTag("T", NBTUtils.writeFloatList(new NBTTagList(), this.translate));
        }

        if (this.scale.x != 1 || this.scale.y != 1 || this.scale.z != 1)
        {
            tag.setTag("S", NBTUtils.writeFloatList(new NBTTagList(), this.scale));
        }

        if (this.rotate.x != 180 || this.rotate.y != 0 || this.rotate.z != 0)
        {
            tag.setTag("R", NBTUtils.writeFloatList(new NBTTagList(), this.rotate));
        }

        if (this.useTarget) tag.setBoolean("Target", this.useTarget);
        if (!this.enabled) tag.setBoolean("Enabled", this.enabled);
        if (!this.limb.isEmpty()) tag.setString("Limb", this.limb);
    }
}