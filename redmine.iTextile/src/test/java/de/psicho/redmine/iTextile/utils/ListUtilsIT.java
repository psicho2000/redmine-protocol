package de.psicho.redmine.iTextile.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ListUtilsIT {

    @Rule
    public TestName currentTestMethod = new TestName();

    private String input;
    private String output;
    private String calculatedOutput;

    @Before
    public void prepare() {
        String canonicalName = this.getClass().getCanonicalName();
        String currentPath = canonicalName.replaceAll("\\.", "/") + "/" + currentTestMethod.getMethodName();

        input = ResourceUtils.readResource(currentPath + ".input.html");
        output = ResourceUtils.readResource(currentPath + ".output.html");
        calculatedOutput = ListUtils.transformLists(input);
    }

    @After
    public void test() {
        assertThat(linyfied(calculatedOutput), is(linyfied(output)));
    }

    @Test
    public void testSimple() {
    }

    @Test
    public void testComplex() {
    }

    private String linyfied(String output) {
        return output.replace(" ", "").replace("\r", "").replace("\n", "").replace("\t", "");
    }
}
