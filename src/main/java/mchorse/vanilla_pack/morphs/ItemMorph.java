package mchorse.vanilla_pack.morphs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMorph extends ItemStackMorph
{
    @SideOnly(Side.CLIENT)
    private static BiMap<String, ItemCameraTransforms.TransformType> transformTypes;

    public ItemStack stack = new ItemStack(Items.DIAMOND_HOE, 1);
    public String transform = "";

    @SideOnly(Side.CLIENT)
    public static BiMap<String, ItemCameraTransforms.TransformType> getTransformTypes()
    {
        if (transformTypes == null)
        {
            transformTypes = HashBiMap.create();

            transformTypes.put("none", ItemCameraTransforms.TransformType.NONE);
            transformTypes.put("third_person_left_hand", ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
            transformTypes.put("third_person_right_hand", ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            transformTypes.put("first_person_left_hand", ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
            transformTypes.put("first_person_right_hand", ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
            transformTypes.put("head", ItemCameraTransforms.TransformType.HEAD);
            transformTypes.put("gui", ItemCameraTransforms.TransformType.GUI);
            transformTypes.put("ground", ItemCameraTransforms.TransformType.GROUND);
            transformTypes.put("fixed", ItemCameraTransforms.TransformType.FIXED);
        }

        return transformTypes;
    }

    public ItemMorph()
    {
        this.name = "item";
    }

    @Override
    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    @SideOnly(Side.CLIENT)
    public ItemCameraTransforms.TransformType getTransformType()
    {
        ItemCameraTransforms.TransformType transformType = transformTypes.get(this.transform);

        return transformType == null ? ItemCameraTransforms.TransformType.NONE : transformType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        scale = scale / 16F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y - 12, 0);
        GlStateManager.scale(scale, scale, scale);

        GuiInventoryElement.drawItemStack(this.stack, -8, -8, 0, null);
        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.disableDepth();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderItem render = Minecraft.getMinecraft().getRenderItem();
        IBakedModel model = render.getItemModelWithOverrides(this.stack, entity.world, entity);

        ItemCameraTransforms.TransformType transform = this.getTransformType();

        if (transform != ItemCameraTransforms.TransformType.NONE)
        {
            model = ForgeHooksClient.handleCameraTransforms(model, transform, false);
        }

        render.renderItem(this.stack, model);

        GlStateManager.popMatrix();

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ItemMorph)
        {
            ItemMorph item = (ItemMorph) obj;

            result = result && ItemStack.areItemStacksEqualUsingNBTShareTag(this.stack, item.stack);
            result = result && this.transform != item.transform;
        }

        return result;
    }

    @Override
    public AbstractMorph create()
    {
        return new ItemMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof ItemMorph)
        {
            ItemMorph item = (ItemMorph) from;

            this.stack = item.stack.copy();
            this.transform = item.transform;
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return target.width;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return target.height;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (!this.stack.isEmpty())
        {
            tag.setTag("Stack", this.stack.serializeNBT());
        }

        tag.setString("Transform", this.transform);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Stack"))
        {
            this.stack = new ItemStack(tag.getCompoundTag("Stack"));
        }

        this.transform = tag.getString("Transform");
    }
}