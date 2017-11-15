package de.psicho.redmine.iTextile.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ListUtilsTest {

    private static final String INPUT_SUFFIX = ".input.html";
    private static final String OUTPUT_SUFFIX = ".output.html";

    @Rule
    public TestName currentTestMethod = new TestName();

    @Test
    public void testSimple() {
        performTest(currentTestMethod.getMethodName());
    }

    @Test
    public void testComplex() {
        performTest(currentTestMethod.getMethodName());
    }

    private void performTest(String methodName) {
        String canonicalName = this.getClass().getCanonicalName();
        String currentPath = canonicalName.replaceAll("\\.", "/") + "/" + methodName;
        String input = ResourceUtils.readResource(currentPath + INPUT_SUFFIX);
        String expectedOutput = ResourceUtils.readResource(currentPath + OUTPUT_SUFFIX);

        String calculatedOutput = ListUtils.transformLists(input);

        assertThat(normalized(calculatedOutput), is(normalized(expectedOutput)));
    }

    private String normalized(String output) {
        return output.replace(" ", "").replace("\r", "").replace("\n", "").replace("\t", "");
    }
}
