package mchorse.metamorph.coremod;

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
        return MetamorphCoremod.obfuscated ? srgName : mcpName; 
    }
}
