package mchorse.metamorph.coremod;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.coremod.transform.TGuiIngameForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = "Metamorph Coremod")
@IFMLLoadingPlugin.TransformerExclusions(value = {"mchorse.metamorph.coremod.","mchorse.metamorph.Metamorph"})
/* The SortingIndex annotation with a value of 1001 or greater makes it so
 * this coremod gets called after Minecraft is deobfuscated when in a
 * release environment. The resulting classes are much easier to read,
 * making debugging easier. The resulting srgname methods and fields
 * are also much less likely to change names from version to version.
 */
@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class MetamorphCoremod implements IFMLLoadingPlugin
{
    
    public static boolean obfuscated = false;

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]
        {
                TGuiIngameForge.class.getName()
        };
    }

    @Override
    public String getModContainerClass()
    {
        return Container.class.getName();
    }
    
    public static class Container extends DummyModContainer
    {
        public Container() {
            super(new ModMetadata());
            ModMetadata meta = getMetadata();
            meta.modId = "metamorph-coremod";
            meta.name = "Metamorph Coremod";
            meta.version = Metamorph.VERSION;
            meta.credits = "";
            meta.authorList = Arrays.asList("mchorse");
            meta.description = "Coremod for the metamorph mod.";
            meta.url = "";
            meta.screenshots = new String[0];
            meta.logoFile = "";
        }
        
        public boolean registerBus(EventBus bus, LoadController controller)
        {
            bus.register(this);
            return true;
        }
    }
    
    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // True means normal Minecraft (srgnames), false means dev environment (mcp names)
        obfuscated = (Boolean)(data.get("runtimeDeobfuscationEnabled"));
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
    
    public static final Logger LOGGER = LogManager.getLogger("Metamorph Coremod");
    
    /**
     * Utility function. If a coremod patch isn't working for some reason,
     * you can stick the transformed bytes through this and get a debug
     * output of the entire class.
     */
    public static void logClassBytesToDebug(byte[] bytes) {
        StringWriter stringWriter = new StringWriter();
        TraceClassVisitor traceVisitor = new TraceClassVisitor(new PrintWriter(stringWriter));
        (new ClassReader(bytes)).accept(traceVisitor, 0);
        LOGGER.debug(stringWriter.getBuffer());
    }
}