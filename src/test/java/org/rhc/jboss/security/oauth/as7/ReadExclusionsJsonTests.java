package org.rhc.jboss.security.oauth.as7;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by nbalkiss on 6/27/17.
 */
public class ReadExclusionsJsonTests {

    @Test
    public void readJsonIntoValidRegexTest() throws Exception{

        File fs = new File("src/test/resources/regexList.txt");

        String string = new String(Files.readAllBytes(fs.toPath()));

        String[] regexArray = string.split(",");

        Arrays.stream(regexArray).map(s -> Pattern.compile(s)).count();

        Assert.assertTrue(regexArray.length == 2);

    }

    @Test
    public void invalidRegexFormatFailsWithPatternException(){
        try {
            String string = "[";

            String[] regexArray = string.split(",");

            Arrays.stream(regexArray).map(s -> Pattern.compile(s)).count();

            Assert.fail();
        }
        catch (PatternSyntaxException patternSyntaxException){

        }
        catch (Exception e){
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void testRegexMatchesCorrectStrings() throws Exception{

        File fs = new File("src/test/resources/regexList.txt");

        String string = new String(Files.readAllBytes(fs.toPath()));

        String[] split = string.split(",");

        Arrays.stream(split).map(s -> Pattern.compile(s)).count();

        Assert.assertTrue(split.length == 2);

        Assert.assertTrue("something.png".matches(split[0]));

        Assert.assertTrue("something/maven2/somethingelse".matches(split[1]));
    }
}
