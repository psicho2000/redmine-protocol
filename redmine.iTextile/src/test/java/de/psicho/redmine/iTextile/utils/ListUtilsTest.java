package de.psicho.redmine.iTextile.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ListUtilsTest {

    @Rule
    public TestName currentTestMethod = new TestName();

    private String input;
    private String output;

    @Before
    public void prepare() {
        String canonicalName = this.getClass().getCanonicalName();
        String currentPath = canonicalName.replaceAll("\\.", "/") + "/" + currentTestMethod.getMethodName();

        input = ResourceUtils.readResource(currentPath + ".input.html");
        output = ResourceUtils.readResource(currentPath + ".output.html");
    }

    @Test
    public void testSimple() {
        String calculatedOutput = ListUtils.transformLists(input);

        assertThat(linyfied(calculatedOutput), is(linyfied(output)));
    }

    private String linyfied(String output) {
        return output.replace(" ", "").replace("\r", "").replace("\n", "");
    }
}
