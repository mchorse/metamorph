package mchorse.metamorph.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.utils.NBTUtils;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.metamorph.api.events.RegisterBlacklistEvent;
import mchorse.metamorph.api.events.RegisterRemapEvent;
import mchorse.metamorph.api.events.RegisterSettingsEvent;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.utils.ISyncableMorph;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MorphUtils
{
    public static boolean isRenderingOnScreen = false;

    /**
     * Generate an empty file
     */
    public static void generateFile(File config, String content)
    {
        config.getParentFile().mkdirs();

        try
        {
            PrintWriter writer = new PrintWriter(config);
            writer.print(content);
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reload blacklist using event
     */
    public static Set<String> reloadBlacklist()
    {
        RegisterBlacklistEvent event = new RegisterBlacklistEvent();
        MinecraftForge.EVENT_BUS.post(event);

        return event.blacklist;
    }

    /**
     * Reload morph settings using event 
     */
    public static Map<String, MorphSettings> reloadMorphSettings()
    {
        RegisterSettingsEvent event = new RegisterSettingsEvent();
        MinecraftForge.EVENT_BUS.post(event);

        return event.settings;
    }

    /**
     * Reload morph ID mappings using event
     */
    public static Map<String, String> reloadRemapper()
    {
        RegisterRemapEvent event = new RegisterRemapEvent();
        MinecraftForge.EVENT_BUS.post(event);

        return event.map;
    }

    /**
     * Render in the world with morph error handling
     */
    @SideOnly(Side.CLIENT)
    public static boolean render(AbstractMorph morph, EntityLivingBase entity, double x, double y, double z, float yaw, float partialTick)
    {
        if (morph == null || morph.errorRendering)
        {
            return false;
        }

        boolean isShadowPass = ReflectionUtils.isOptifineShadowPass();

        if (!GuiModelRenderer.isRendering() && (isShadowPass && morph.getSettings().shadowOption == 1 || !isShadowPass && morph.getSettings().shadowOption == 2))
        {
            return false;
        }

        return renderDirect(morph, entity, x, y, z, yaw, partialTick);
    }

    /* Without shadow pass check */
    @SideOnly(Side.CLIENT)
    public static boolean renderDirect(AbstractMorph morph, EntityLivingBase entity, double x, double y, double z, float yaw, float partialTick)
    {
        if (morph == null || morph.errorRendering)
        {
            return false;
        }

        try
        {
            morph.render(entity, x, y, z, yaw, partialTick);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            morph.errorRendering = true;
        }
        finally
        {
            try
            {
                Tessellator.getInstance().getBuffer().finishDrawing();
                System.err.println("Unfinished builder comes from class: " + morph.getClass().getName());
            }
            catch (IllegalStateException ex)
            {}
        }

        return false;
    }

    /**
     * Render on screen with morph error handling
     */
    @SideOnly(Side.CLIENT)
    public static boolean renderOnScreen(AbstractMorph morph, EntityPlayer player, int x, int y, float scale, float alpha)
    {
        if (morph == null || morph.errorRendering)
        {
            return false;
        }

        try
        {
            isRenderingOnScreen = true;
            morph.renderOnScreen(player, x, y, scale, alpha);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            morph.errorRendering = true;
        }
        finally
        {
            isRenderingOnScreen = false;
            try
            {
                Tessellator.getInstance().getBuffer().finishDrawing();
                System.err.println("Unfinished builder comes from class: " + morph.getClass().getName());
            }
            catch (IllegalStateException ex)
            {}
        }

        return false;
    }

    /**
     * Copy a morph
     */
    public static AbstractMorph copy(AbstractMorph morph)
    {
        return morph == null ? null : morph.copy();
    }

    /**
     * Pause given morph
     */
    public static boolean pause(AbstractMorph morph, AbstractMorph previous, int offset)
    {
        if (morph instanceof ISyncableMorph)
        {
            ((ISyncableMorph) morph).pause(previous, offset);

            return true;
        }
        else if (morph instanceof IBodyPartProvider)
        {
            ((IBodyPartProvider) morph).getBodyPart().pause(previous, offset);

            return true;
        }

        return false;
    }
    
    /**
     * Resume given morph from pause.
     */
    public static boolean resume(AbstractMorph morph)
    {
        if (morph instanceof ISyncableMorph)
        {
            ((ISyncableMorph) morph).resume();
            
            return true;
        }
        else if (morph instanceof IBodyPartProvider)
        {
            for (BodyPart part : ((IBodyPartProvider) morph).getBodyPart().parts)
            {
                if (!part.morph.isEmpty() && part.morph.get() instanceof ISyncableMorph)
                {
                    ((ISyncableMorph) part.morph.get()).resume();
                }
            }
            
            return true;
        }

        return false;
    }

    /**
     * Morph to NBT
     */
    public static NBTTagCompound toNBT(AbstractMorph morph)
    {
        if (morph == null)
        {
            return null;
        }

        return morph.toNBT();
    }

    /**
     * Write a morph to {@link ByteBuf}
     *
     * This method will simply write a boolean indicating whether a morph was
     * saved and morph's data.
     *
     * Important: use this method in conjunction with
     * {@link #morphFromBuf(ByteBuf)}
     */
    public static void morphToBuf(ByteBuf buffer, AbstractMorph morph)
    {
        ByteBufUtils.writeTag(buffer, morph == null ? null : morph.toNBT());
    }

    /**
     * Create a morph from {@link ByteBuf}
     *
     * This method will read a morph from {@link ByteBuf} which should contain
     * a boolean indicating whether a morph was written and the morph data.
     *
     * Important: use this method in conjunction with
     * {@link #morphToBuf(ByteBuf, AbstractMorph)}!
     */
    public static AbstractMorph morphFromBuf(ByteBuf buffer)
    {
        return MorphManager.INSTANCE.morphFromNBT(NBTUtils.readInfiniteTag(buffer));
    }

    /**
     * Traverses the morph and its bodyparts to find a morph that matches the provided predicate.
     * @param morph
     * @param condition the predicate to apply on the morphs and children
     * @return true if any of the morph and children match the provided predicate, otherwise false.
     */
    public static boolean anyMatch(AbstractMorph morph, Predicate<AbstractMorph> condition)
    {
        while (true)
        {
            if (condition.test(morph))
            {
                return true;
            }

            if (morph instanceof IBodyPartProvider)
            {
                BodyPartManager mgr = ((IBodyPartProvider) morph).getBodyPart();
                for (BodyPart part : mgr.parts)
                {
                    if (part.enabled)
                    {
                        if (anyMatch(part.morph.get(), condition))
                        {
                            return true;
                        }
                    }
                }
            }

            if (morph instanceof IMorphProvider)
            {
                morph = ((IMorphProvider) morph).getMorph();
            }
            else
            {
                break;
            }
        }

        return false;
    }
}