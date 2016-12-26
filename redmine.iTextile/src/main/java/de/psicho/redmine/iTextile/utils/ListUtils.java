package de.psicho.redmine.iTextile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// @formatter:off

// "Nested lists don't work in a cell"
// http://developers.itextpdf.com/de/node/2243
//
// This is true only for standard HTML. For non standard this can be done in cells aswell. 

// Conclusion:
//     * MarkupParser() creates correct HTML
//     * XMLWorkerHelper can use this HTML outside of table
//     * in tables we can use the following hack
// 
// Transform:                      to:
//     <ul>                            <ul>
//         <li>foo                         <li>foo</li>
//             <ul>                        <ul>
//                 <li>bar</li>                <li>bar</li>
//             </ul>                       </ul>
//         </li>                       
//     </ul>                           </ul>

//@formatter:on

public class ListUtils {

    // *? non greedy search
    // ?= positive look-ahead
    private static final Pattern pattern = Pattern.compile("(<li>.*?)(?=<ol>|<ul>|<\\/ul>|<\\/ol>|<li>)", Pattern.DOTALL);

    public static String transformLists(String input) {
        String strippedInput = input.replace("</li>", "");
        Matcher matcher = pattern.matcher(strippedInput);
        return matcher.replaceAll("$1</li>");
    }
}
