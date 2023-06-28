package mchorse.vanilla_pack.render;

import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ItemExtruder
{
    private static Map<ResourceLocation, CachedExtrusion> cache = new HashMap<ResourceLocation, CachedExtrusion>();

    public static CachedExtrusion extrude(ResourceLocation texture)
    {
        CachedExtrusion extrusion = cache.get(texture);

        if (extrusion != null)
        {
            return extrusion;
        }

        BufferedImage pixels = null;

        try
        {
            pixels = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());
        }
        catch (Exception e)
        {
            cache.put(texture, null);

            return null;
        }

        int w = pixels.getWidth();
        int h = pixels.getHeight();
        extrusion = new CachedExtrusion(texture, w, h);

        int uv_x = 0;
        int uv_y = 0;

        float p = 0.5F;
        float n = -0.5F;
        float u1 = uv_x / (float) w;
        float v1 = uv_y / (float) h;
        float u2 = (uv_x + w) / (float) w;
        float v2 = (uv_y + h) / (float) h;
        float d = 0.5F / 16F;

        fillTexturedNormalQuad(extrusion,
            p, n, d,
            n, n, d,
            n, p, d,
            p, p, d,
            u1, v1, u2, v2,
            0F, 0F, 1F
        );

        fillTexturedNormalQuad(extrusion,
            n, n, -d,
            p, n, -d,
            p, p, -d,
            n, p, -d,
            u2, v1, u1, v2,
            0F, 0F, -1F
        );

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                int x = uv_x + i;
                int y = uv_y + j;

                if (hasPixel(pixels, x, y))
                {
                    generateNeighbors(pixels, extrusion, i, j, x, y, d, w, h);
                }
            }
        }

        extrusion.flush();

        cache.put(texture, extrusion);

        return extrusion;
    }

    private static void generateNeighbors(BufferedImage pixels, CachedExtrusion extrusion, int i, int j, int x, int y, float d, float w, float h)
    {
        float u = (x + 0.5F) / w;
        float v = (y + 0.5F) / h;

        if (!hasPixel(pixels, x - 1, y) || i == 0)
        {
            fillTexturedNormalQuad(extrusion,
                i / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -j / h + 0.5F, -d,
                i / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, d,
                u, v, u, v,
                -1F, 0F, 0F
            );
        }

        if (!hasPixel(pixels, x + 1, y) || i == 15)
        {
            fillTexturedNormalQuad(extrusion,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, -d,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                u, v, u, v,
                1F, 0F, 0F
            );
        }

        if (!hasPixel(pixels, x, y - 1) || j == 0)
        {
            fillTexturedNormalQuad(extrusion,
                (i + 1) / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -j / h + 0.5F, -d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, -d,
                u, v, u, v,
                0F, 1F, 0F
            );
        }

        if (!hasPixel(pixels, x, y + 1) || j == 15)
        {
            fillTexturedNormalQuad(extrusion,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, d,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, d,
                u, v, u, v,
                0F, -1F, 0F
            );
        }
    }

    private static boolean hasPixel(BufferedImage pixels, int x, int y)
    {
        if (x < 0 || x >= pixels.getWidth() || y < 0 || y >= pixels.getHeight())
        {
            return false;
        }

        Color pixel = new Color().set(pixels.getRGB(x, y));

        return pixel != null && pixel.a >= 1;
    }

    /**
     * Fill a quad for vertex-normal-uv-rgba. Points should
     * be supplied in this order:
     *
     *     3 -------> 4
     *     ^
     *     |
     *     |
     *     2 <------- 1
     *
     * I.e. bottom left, bottom right, top left, top right, where left is -X and right is +X,
     * in case of a quad on fixed on Z axis.
     */
    public static void fillTexturedNormalQuad(CachedExtrusion extrusion, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, float nx, float ny, float nz)
    {
        /* 1 - BL, 2 - BR, 3 - TR, 4 - TL */
        extrusion.addVertex(x2, y2, z2, nx, ny, nz, u1, v2);
        extrusion.addVertex(x1, y1, z1, nx, ny, nz, u2, v2);
        extrusion.addVertex(x4, y4, z4, nx, ny, nz, u2, v1);

        extrusion.addVertex(x2, y2, z2, nx, ny, nz, u1, v2);
        extrusion.addVertex(x4, y4, z4, nx, ny, nz, u2, v1);
        extrusion.addVertex(x3, y3, z3, nx, ny, nz, u1, v1);
    }
}