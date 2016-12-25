package de.psicho.redmine.iTextile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// @formatter:off

// "Nested lists don't work in a cell"
// http://developers.itextpdf.com/de/node/2243
//
// This is true only for standard HTML. For non standard this can be done in cells aswell. 

// 
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

    // *? = non greedy
    private static final Pattern pattern = Pattern.compile("li>(.*?)<", Pattern.DOTALL);

    public static String transformLists(String input) {
        String intermediate = input.replace("</li>", "");
        Matcher matcher = pattern.matcher(intermediate);
        String replaced = matcher.replaceAll("li>$1</li><");
        return replaced;
    }
}
