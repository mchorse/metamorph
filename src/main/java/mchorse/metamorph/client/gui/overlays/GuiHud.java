package mchorse.metamorph.client.gui.overlays;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;

public class GuiHud extends Gui
{
    /**
     * Whether or not squid air should be rendered
     * in place of regular player air.
     * Checked by RenderingHandler
     */
    public boolean renderSquidAir = false;
    public int squidAir = 300;

    public void renderSquidAir(int width, int height)
    {
        if (squidAir < 300)
        {
            GlStateManager.enableBlend();
            int left = width / 2 + 91;
            int top = height - GuiIngameForge.right_height;

            int full = MathHelper.ceil((squidAir - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceil(squidAir * 10.0D / 300.0D) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
            }

            GuiIngameForge.right_height += 10;
            GlStateManager.disableBlend();
        }
    }
}
