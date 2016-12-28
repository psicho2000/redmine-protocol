package de.psicho.redmine.iTextile.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import de.psicho.redmine.iTextile.ProcessingException;

public class ResourceUtils {

    public static String readResource(String resourceName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(resourceName);

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resourceStream))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new ProcessingException(ex);
        }
    }
}
