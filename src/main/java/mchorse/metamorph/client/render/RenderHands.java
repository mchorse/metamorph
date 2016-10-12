package mchorse.metamorph.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderSpecificHandEvent;

/**
 * Render hands
 * 
 * This class is responsible for rendering hands in first person. There's 
 * lots of stuff to consider when dealing with first person. I wonder how 
 * hard it will be to implement. It was pretty hard.
 */
public class RenderHands
{
    private RenderPlayer render;

    public RenderHands(RenderPlayer render)
    {
        this.render = render;
    }

    /**
     * Renders the hand in first person.
     * 
     * Talk to the HAND! 
     */
    public void render(EntityPlayer player, RenderSpecificHandEvent event)
    {
        event.setCanceled(true);

        EnumHandSide side = event.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float swing = event.getHand() == EnumHand.MAIN_HAND ? player.getSwingProgress(event.getPartialTicks()) : 0;
        float progress = event.getEquipProgress();

        GlStateManager.pushMatrix();
        this.setup(swing, progress, side);
        GlStateManager.disableCull();

        if (side == EnumHandSide.LEFT)
        {
            this.render.renderLeftArm(player);
        }
        else
        {
            this.render.renderRightArm(player);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Setup transformations. Taken from {@link ItemRenderer}'s private method 
     * renderArm or something similar.
     */
    private void setup(float swing, float progress, EnumHandSide side)
    {
        boolean flag = side != EnumHandSide.LEFT;

        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt_float(swing);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swing * (float) Math.PI);

        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + progress * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);

        float f5 = MathHelper.sin(swing * swing * (float) Math.PI);
        float f6 = MathHelper.sin(f1 * (float) Math.PI);

        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
    }
}