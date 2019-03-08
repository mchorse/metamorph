package mchorse.metamorph.util;

import javax.annotation.Nonnull;

import net.minecraftforge.classloading.FMLForgePlugin;

/**
 * Object for storing a method/field string. If a srgname
 * is given, it will provide the appropriate mcp name if
 * in a development environment environment. Otherwise
 * it will just provide the given string.
 * 
 * @author asanetargoss
 */
public class ObfuscatedName
{
    private final String srgName;
    /** Initially null. Given cached value when get()
     * is called in a development environment */
    private String mcpName = null;
    
    public ObfuscatedName(@Nonnull String srgName)
    {
        this.srgName = srgName;
    }
    
    /**
     * Get name relevant for current environment.
     * In development environment, default to srgName
     * if mcpName is not present in the mapping.
     */
    public @Nonnull String getName()
    {
        if (FMLForgePlugin.RUNTIME_DEOBF)
        {
            return srgName;
        }
        else
        {
            if (mcpName == null)
            {
                mcpName = DevMappings.get(srgName);
                if (mcpName == null)
                {
                    mcpName = srgName;
                }
            }
            return mcpName;
        }
    }
}
