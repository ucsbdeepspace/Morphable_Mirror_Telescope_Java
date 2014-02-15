package edu.ucsb.deepspace.resource;

import java.util.Properties;


/*
 * This is a starting point for dynamically loading .jar files based on operating system.
 * This would be helpful if some developers work on windows and others work on mac.
 * 
 * See http://stackoverflow.com/questions/20072622/including-compiled-library-file-for-java-project-in-different-operating-system
 * for more information.
 *
 */
public class PlatformParser {
	public static String nativelibArch() {
        return genericOs() + "-" + System.getProperty("os.arch");
    }

    public static String genericOs() {
        return genericOs(System.getProperties());
    }

    public static String genericOs(Properties sysprops)
    {
        String osname = sysprops.getProperty("os.name");
        if (osname == null)
            return null;
        osname = osname.toLowerCase();
        if (osname.startsWith("linux"))
            return "linux";
        if (osname.startsWith("windows"))
            return "win32";
        if (osname.startsWith("freebsd"))
            return "freebsd";
        if (osname.startsWith("solaris"))
            return "solaris";
        if (osname.equals("aix"))
            return "aix";
        if (osname.startsWith("digital unix"))
            return "digital-unix";
        if (osname.equals("hp ux"))
            return "hpux";
        if (osname.equals("irix"))
            return "irix";
        if (osname.startsWith("mac os"))
            return "macos";
        if (osname.equals("mpe/ix"))
            return "mpe-ix";
        if (osname.equals("os/2"))
            return "os-2";
        if (osname.startsWith("netware"))
            return "netware";
        return null;
    }
}
