package de.psicho.redmine.iTextile.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import de.psicho.redmine.iTextile.ProcessingException;

public class ResourceUtils {

    public static String readResource(String resourceName) {
        ClassLoader classLoader = new ResourceUtils().getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        String fileContents = StringUtils.EMPTY;
        try {
            fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException ex) {
            throw new ProcessingException(ex);
        }
        return fileContents;
    }
}
