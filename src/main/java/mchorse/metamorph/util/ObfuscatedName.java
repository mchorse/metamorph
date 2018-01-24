package mchorse.metamorph.util;

import net.minecraftforge.classloading.FMLForgePlugin;

public class ObfuscatedName 
{
    private String mcpName;
    private String srgName;
    
    public ObfuscatedName(String mcpName, String srgName)
    {
        this.mcpName = mcpName;
        this.srgName = srgName;
    }
    
    public String getName()
    {
        return FMLForgePlugin.RUNTIME_DEOBF ? srgName : mcpName; 
    }
}
