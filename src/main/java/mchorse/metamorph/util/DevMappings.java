package mchorse.metamorph.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;

/**
 * Utility class for converting srgnames
 * (used in a release environment)
 * to mcp names (used in a development environment)
 * Protects function/field string constants from
 * the whims of mcp renames.
 * 
 * For use in a development environment only.
 * 
 * @author asanetargoss
 */
public class DevMappings
{
    static Map<String, String> srgToMcp;
    
    /**
     * Convert srgnames to mcp names
     * (methods and fields) in a dev environment.
     */
    public static String get(String srgname)
    {
        return srgToMcp.get(srgname);
    }
    
    static
    {
        String mappingFileProp = System.getProperty("net.minecraftforge.gradle.GradleStart.srg.srg-mcp");
        if (!Strings.isNullOrEmpty(mappingFileProp))
        {
            try
            {
                srgToMcp = new HashMap<String, String>();
                
                List<String> mappingLines = Files.readLines(new File(mappingFileProp), Charsets.UTF_8);
                for (String line : mappingLines)
                {
                    /* Get rid of all the
                     * fully qualified junk since the srgnames
                     * themselves should be unique.
                     */
                    String[] parts = line.split(" ");
                    if (parts[0].equals("FD:"))
                    {
                        // Field: check 1st and 2rd ending part (after the slashes)
                        String srg = parts[1].substring(parts[1].lastIndexOf("/")+1);
                        String mcp = parts[2].substring(parts[2].lastIndexOf("/")+1);
                        srgToMcp.put(srg, mcp);
                    }
                    else if (parts[0].equals("MD:"))
                    {
                        // Method: check 1st and 3rd ending part (after the slashes)
                        //     (the other two parts are the descriptors)
                        String srg = parts[1].substring(parts[1].lastIndexOf("/")+1);
                        String mcp = parts[3].substring(parts[3].lastIndexOf("/")+1);
                        srgToMcp.put(srg, mcp);
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
}
