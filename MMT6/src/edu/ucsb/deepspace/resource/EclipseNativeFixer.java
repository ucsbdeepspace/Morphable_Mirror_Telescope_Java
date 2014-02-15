package edu.ucsb.deepspace.resource;

import java.lang.reflect.Field;

public class EclipseNativeFixer extends ClassLoader {
	public static void setLibraryPath(String path) throws Exception {
        System.setProperty("java.library.path", path);

        //set sys_paths to null
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

    public EclipseNativeFixer(ClassLoader parent) {
        super(parent);

        String nativelibArch = PlatformParser.nativelibArch();
        String lipbat = System.getProperty("java.library.path");
        if (lipbat != null) {
            lipbat = lipbat.replaceAll("nativelibArch", nativelibArch);
            try {
            setLibraryPath(lipbat);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
