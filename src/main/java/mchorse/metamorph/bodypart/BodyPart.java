package mchorse.metamorph.bodypart;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Objects;

import mchorse.mclib.client.Draw;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.Animation;
import mchorse.metamorph.api.morphs.utils.IAnimationProvider;
import mchorse.metamorph.api.morphs.utils.IMorphGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
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
public class BodyPart
{
    public static Vector3f cachedTranslation = new Vector3f();
    public static Vector3f cachedAngularVelocity = new Vector3f();
    public static Matrix4f modelViewMatrix = new Matrix4f();
    public static boolean recording = false;

    @SideOnly(Side.CLIENT)
    public static void recordMatrix(AbstractMorph parent, EntityLivingBase entity, float partialTicks)
    {
        recording = true;

        int lastMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        MorphUtils.renderDirect(parent, entity, 0, 0, 0, 0, partialTicks);
        GL11.glPopMatrix();

        GL11.glMatrixMode(lastMatrixMode);

        recording = false;
    }

    public Morph morph = new Morph();
    public ItemStack[] slots = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    public Vector3f translate = new Vector3f();
    public Vector3f scale = new Vector3f(1, 1, 1);
    public Vector3f rotate = new Vector3f(180, 0, 0);
    public boolean useTarget = false;
    public boolean enabled = true;
    public boolean animate = true;
    public String limb = "";

    private EntityLivingBase entity;

    private Vector3f lastTranslate;
    private Vector3f lastScale;
    private Vector3f lastRotate;
    private Vector3f previousRotation = new Vector3f();
    
    public Matrix4f lastMatrix = null;

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
    public void render(AbstractMorph parent, EntityLivingBase entity, float partialTicks)
    {
    	if (recording)
    	{
    		this.lastMatrix = MatrixUtils.readModelView(new Matrix4f());
    		return;
    	}
    	
        entity = this.useTarget ? entity : this.entity;

        if (this.morph.get() == null || entity == null || !this.enabled)
        {
            return;
        }

        Animation animation = parent instanceof IAnimationProvider ? ((IAnimationProvider) parent).getAnimation() : null;

        float tx = this.translate.x;
        float ty = this.translate.y;
        float tz = this.translate.z;
        float sx = this.scale.x;
        float sy = this.scale.y;
        float sz = this.scale.z;
        float rx = this.rotate.x;
        float ry = this.rotate.y;
        float rz = this.rotate.z;

        if (animation != null && animation.isInProgress() && this.lastTranslate != null && this.animate)
        {
            Interpolation inter = animation.interp;
            float factor = animation.getFactor(partialTicks);

            tx = inter.interpolate(this.lastTranslate.x, tx, factor);
            ty = inter.interpolate(this.lastTranslate.y, ty, factor);
            tz = inter.interpolate(this.lastTranslate.z, tz, factor);
            sx = inter.interpolate(this.lastScale.x, sx, factor);
            sy = inter.interpolate(this.lastScale.y, sy, factor);
            sz = inter.interpolate(this.lastScale.z, sz, factor);
            rx = inter.interpolate(this.lastRotate.x, rx, factor);
            ry = inter.interpolate(this.lastRotate.y, ry, factor);
            rz = inter.interpolate(this.lastRotate.z, rz, factor);
        }

        if (!this.morph.isEmpty())
        {
            MatrixUtils.Transformation modelView = new MatrixUtils.Transformation();

            if (MatrixUtils.matrix != null)
            {
                modelView = MatrixUtils.extractTransformations(MatrixUtils.matrix, MatrixUtils.readModelView(modelViewMatrix));
            }

            /* translation */

            this.morph.get().cachedTranslation.set(cachedTranslation);

            Vector3f translate = new Vector3f(tx, ty, tz);
            Matrix3f transformation = new Matrix3f(modelView.getRotation3f());

            transformation.mul(modelView.getScale3f());
            transformation.transform(translate);

            this.morph.get().cachedTranslation.add(translate);

            /* angular velocity */
            /*Matrix3f rotation1 = MatrixUtils.getZYXrotationMatrix(rx, ry, rz);
            Matrix3f rotation0 = MatrixUtils.getZYXrotationMatrix(this.previousRotation.x, this.previousRotation.y, this.previousRotation.z);
            rotation0.invert();
            rotation1.mul(rotation0);
            this.morph.get().angularVelocity.set(MatrixUtils.getAngularVelocity(rotation1));
            this.morph.get().angularVelocity.add(cachedAngularVelocity);
            if (this.morph.get().age != this.lastAge) this.previousRotation.set(rx, ry, rz);
            this.lastAge = this.morph.get().age;*/
        }

        cachedTranslation.set(0,0,0);
        //cachedAngularVelocity.set(0,0,0);

        GL11.glPushMatrix();
        GL11.glTranslatef(tx, ty, tz);

        GL11.glRotatef(rz, 0, 0, 1);
        GL11.glRotatef(ry, 0, 1, 0);
        GL11.glRotatef(rx, 1, 0, 0);

        GL11.glScalef(sx, sy, sz);

        float yaw = entity.rotationYaw;
        float prevYaw = entity.prevRotationYaw;
        float rotationYaw = entity.renderYawOffset;
        float prevRotationYaw = entity.prevRenderYawOffset;
        float rotationYawHead = entity.rotationYawHead;
        float prevRotationYawHead = entity.prevRotationYawHead;

        entity.rotationYaw = entity.rotationYaw - entity.renderYawOffset;
        entity.prevRotationYaw = entity.prevRotationYaw - entity.prevRenderYawOffset;
        entity.rotationYawHead = entity.rotationYawHead - entity.renderYawOffset;
        entity.prevRotationYawHead = entity.prevRotationYawHead - entity.prevRenderYawOffset;
        entity.renderYawOffset = entity.prevRenderYawOffset = 0;

        MorphUtils.render(this.morph.get(), entity, 0, 0, 0, 0, partialTicks);

        entity.rotationYaw = yaw;
        entity.prevRotationYaw = prevYaw;
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

            if (Metamorph.renderBodyPartAxis.get())
            {
                Draw.axis(0.1F);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
        }

        GL11.glPopMatrix();
    }

    public void update(AbstractMorph parent, EntityLivingBase entity)
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
        this.morph.set(part.morph.copy());

        if (Objects.equal(this.limb, part.limb))
        {
            this.lastTranslate = new Vector3f(this.translate);
            this.lastScale = new Vector3f(this.scale);
            this.lastRotate = new Vector3f(this.rotate);
        }
        else
        {
            this.lastTranslate = null;
            this.lastScale = null;
            this.lastRotate = null;
        }

        this.translate.set(part.translate);
        this.scale.set(part.scale);
        this.rotate.set(part.rotate);
        this.useTarget = part.useTarget;
        this.enabled = part.enabled;
        this.animate = part.animate;

        for (int i = 0; i < part.slots.length; i++)
        {
            this.slots[i] = part.slots[i];
        }

        this.limb = part.limb;

        return true;
    }

    public void pause(BodyPart previous, int offset)
    {
        if (previous != null && Objects.equal(this.limb, previous.limb))
        {
            this.lastTranslate = new Vector3f(previous.translate);
            this.lastScale = new Vector3f(previous.scale);
            this.lastRotate = new Vector3f(previous.rotate);
        }

        MorphUtils.pause(this.morph.get(), previous == null ? null : previous.morph.get(), offset);
    }

    public BodyPart genCurrentBodyPart(AbstractMorph morph, float partialTicks)
    {
        BodyPart part = this.copy();

        if (morph instanceof IAnimationProvider)
        {
            Animation animation = ((IAnimationProvider) morph).getAnimation();

            if (animation.isInProgress() && this.lastTranslate != null && this.animate)
            {
                Interpolation inter = animation.interp;
                float factor = animation.getFactor(partialTicks);

                part.translate.x = inter.interpolate(this.lastTranslate.x, this.translate.x, factor);
                part.translate.y = inter.interpolate(this.lastTranslate.y, this.translate.y, factor);
                part.translate.z = inter.interpolate(this.lastTranslate.z, this.translate.z, factor);
                part.scale.x = inter.interpolate(this.lastScale.x, this.scale.x, factor);
                part.scale.y = inter.interpolate(this.lastScale.y, this.scale.y, factor);
                part.scale.z = inter.interpolate(this.lastScale.z, this.scale.z, factor);
                part.rotate.x = inter.interpolate(this.lastRotate.x, this.rotate.x, factor);
                part.rotate.y = inter.interpolate(this.lastRotate.y, this.rotate.y, factor);
                part.rotate.z = inter.interpolate(this.lastRotate.z, this.rotate.z, factor);
            }
        }

        if (this.morph.get() instanceof IMorphGenerator)
        {
            IMorphGenerator generator = (IMorphGenerator) this.morph.get();

            if (generator.canGenerate())
            {
                part.morph.setDirect(generator.genCurrentMorph(partialTicks));
            }
        }

        return part;
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
            result = result && this.animate == part.animate;

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
        part.animate = this.animate;

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
        if (tag.hasKey("Animate")) this.animate = tag.getBoolean("Animate");
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
        int empty = 0;

        for (int i = 0; i < this.slots.length; i++)
        {
            NBTTagCompound compound = new NBTTagCompound();
            ItemStack stack = this.slots[i];

            if (!stack.isEmpty())
            {
                stack.writeToNBT(compound);
            }
            else
            {
                empty += 1;
            }

            list.appendTag(compound);
        }

        if (empty != this.slots.length)
        {
            tag.setTag("Items", list);
        }

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
        if (!this.animate) tag.setBoolean("Animate", this.animate);
        if (!this.limb.isEmpty()) tag.setString("Limb", this.limb);
    }
}