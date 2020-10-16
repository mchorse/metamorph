package mchorse.vanilla_pack.morphs;

import com.google.common.base.Objects;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Block morph
 * 
 * This morph allows players to disguise themselves as blocks.
 */
public class BlockMorph extends ItemStackMorph
{
    /**
     * Block state to render, doesn't really mean anything on the server 
     * side. Used for rendering 
     */
    public IBlockState block = Blocks.STONE.getDefaultState();

    /**
     * Block position to lock the target in
     */
    public BlockPos blockPos;

    /**
     * Set the name 
     */
    public BlockMorph()
    {
        this.name = "block";
    }

    @Override
    public void setStack(ItemStack stack)
    {
        if (stack.getItem() instanceof ItemBlock)
        {
            this.block = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
        }
    }

    @Override
    public ItemStack getStack()
    {
        return new ItemStack(this.block.getBlock(), 1, this.block.getBlock().getMetaFromState(this.block));
    }

    /**
     * Render in GUIs just like any other entity, 45 degree rotate  by 
     * X and Y. 
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.enableDepth();
        BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale((-scale), -scale, -scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0, 1, 0);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        blockrendererdispatcher.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }

    /**
     * Render the block morph on player's position
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        GlStateManager.enableRescaleNormal();
        BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }

    /**
     * Update the entity
     * 
     * This particular morph sets the AABB to almost 1 (so it would be 
     * easy enough to slip into 1x1 block area, and also so other players 
     * won't see the AABB when debug AABB is enabled).
     * 
     * This method as well locks the character in block position if 
     * it has the block position.
     */
    @Override
    public void update(EntityLivingBase target)
    {
        super.update(target);

        if (this.blockPos != null)
        {
            target.motionX = target.motionY = target.motionZ = 0;
            target.setPosition(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5);
        }

        this.updateSize(target, getWidth(target), getHeight(target));
    }

    @Override
    public AbstractMorph create()
    {
        return new BlockMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof BlockMorph)
        {
            BlockMorph morph = (BlockMorph) from;

            this.block = morph.block;
            this.blockPos = morph.blockPos;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof BlockMorph)
        {
            BlockMorph morph = (BlockMorph) obj;

            result = result && Objects.equal(morph.block, this.block);
            result = result && Objects.equal(morph.blockPos, this.blockPos);
        }

        return result;
    }

    @Override
    @SuppressWarnings(value = {"deprecation"})
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Block"))
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("Block")));

            if (block != null)
            {
                this.block = tag.hasKey("Meta") ? block.getStateFromMeta(tag.getByte("Meta")) : block.getDefaultState();
            }
        }

        if (tag.hasKey("Pos"))
        {
            int[] pos = tag.getIntArray("Pos");

            if (pos.length == 3)
            {
                this.blockPos = new BlockPos(pos[0], pos[1], pos[2]);
            }
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.block != null)
        {
            tag.setString("Block", ForgeRegistries.BLOCKS.getKey(this.block.getBlock()).toString());
            tag.setByte("Meta", (byte) this.block.getBlock().getMetaFromState(this.block));
        }

        if (this.blockPos != null)
        {
            tag.setIntArray("Pos", new int[] {this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()});
        }
    }

    @Override
    public float getWidth(EntityLivingBase target)
    {
        return 0.99F;
    }

    @Override
    public float getHeight(EntityLivingBase target)
    {
        return 0.99F;
    }
}