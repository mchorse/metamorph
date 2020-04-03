package mchorse.vanilla_pack.morphs;

import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class ItemMorph extends ItemStackMorph
{
	public ItemStack stack = new ItemStack(Items.DIAMOND_HOE, 1);

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

	@Override
	public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
	{
		GlStateManager.disableCull();
		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		scale = scale / 16F;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 12, -200);
		GlStateManager.scale(scale, scale, scale);

		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
		FontRenderer font = null;
		if (!this.stack.isEmpty()) {
			font = this.stack.getItem().getFontRenderer(this.stack);
		}

		if (font == null) {
			font = Minecraft.getMinecraft().fontRenderer;
		}

		itemRender.renderItemAndEffectIntoGUI(this.stack, -8, -8);
		itemRender.renderItemOverlayIntoGUI(font, this.stack, -8, -8, null);

		GlStateManager.popMatrix();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableCull();
		GlStateManager.disableDepth();
	}

	@Override
	public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		RenderItem render = Minecraft.getMinecraft().getRenderItem();
		IBakedModel model = render.getItemModelWithOverrides(stack, entity.world, entity);

		render.renderItem(this.stack, model);

		GlStateManager.popMatrix();
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean result = super.equals(obj);

		if (obj instanceof ItemMorph)
		{
			ItemMorph item = (ItemMorph) obj;

			result = result && ItemStack.areItemStacksEqualUsingNBTShareTag(this.stack, item.stack);
		}

		return result;
	}

	@Override
	public AbstractMorph create(boolean isRemote)
	{
		return new ItemMorph();
	}

	@Override
	public void copy(AbstractMorph from, boolean isRemote)
	{
		super.copy(from, isRemote);

		if (from instanceof ItemMorph)
		{
			this.stack = ((ItemMorph) from).stack.copy();
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
	}

	@Override
	public void fromNBT(NBTTagCompound tag)
	{
		super.fromNBT(tag);

		if (tag.hasKey("Stack"))
		{
			this.stack = new ItemStack(tag.getCompoundTag("Stack"));
		}
	}
}