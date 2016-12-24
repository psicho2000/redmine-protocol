package de.psicho.redmine.iTextile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListUtils {

    public static String transformLists(String input) {
        Pattern pattern = Pattern.compile("/<li>(.*)<(?!\\/li>)/Us");
        Matcher matcher = pattern.matcher(input);
        String replaced = matcher.replaceAll("<li>$1</li><$2");
        return replaced;
    }
}
