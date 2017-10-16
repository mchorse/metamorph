package mchorse.metamorph.client.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class GuiHud extends Gui
{
    public boolean renderSquidAir = false;
    public int squidAir = 300;
    
    public void renderSquidAir(int width, int height, RenderGameOverlayEvent eventParent)
    {
        if (this.renderSquidAir && squidAir < 300)
        {
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
            int actualAir = player.getAir();
            player.setAir(squidAir);
            
            if (!MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, ElementType.AIR)))
            {
                GlStateManager.enableBlend();
                int left = width/2 + 91;
                int top = height - GuiIngameForge.right_height;
                
                int full = MathHelper.ceiling_double_int((double)(squidAir - 2) * 10.0D / 300.0D);
                int partial = MathHelper.ceiling_double_int((double)squidAir * 10.0D / 300.0D) - full;
                for (int i = 0; i < full + partial; ++i)
                {
                    drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
                }
                
                GuiIngameForge.right_height += 10;
                GlStateManager.disableBlend();
                
                MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, ElementType.AIR));
            }
            
            player.setAir(actualAir);
        }
    }
}
