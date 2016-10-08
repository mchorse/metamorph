package mchorse.metamorph;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

/**
 * Metamorph mod
 * 
 * This mod provides functionality for survival morphing. To gain a morph 
 * you have to kill a mob. Once you killed it, you gain its morphing. Once you 
 * gained its morphing you can use special menu to select a mob into which to 
 * morph.
 * 
 * Except different shape, you gain also special abilities for specific mobs. 
 * In creative you can access all morphings. 
 */
@Mod(modid = Metamorph.MODID, name = Metamorph.MODNAME, version = Metamorph.VERSION)
public class Metamorph
{
    public static final String MODID = "metamorph";
    public static final String MODNAME = "Metamorph";
    public static final String VERSION = "1.0";

    public static final String CLIENT_PROXY = "mchorse.metamorph.ClientProxy";
    public static final String SERVER_PROXY = "mchorse.metamorph.ServerProxy";

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;
}