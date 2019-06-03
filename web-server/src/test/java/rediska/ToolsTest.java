package rediska;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class ToolsTest {

    private Properties cachedData = new Properties();
    private final String FILE_NAME =  "C:\\temp\\java\\" + getClass().getSimpleName() + ".txt";
    private final String COMMENTS = "No Comments";
    private final Set<Integer> uniqueNumbers = new HashSet<>();

    //TODO refactor setup and helper methods to helper class
    @BeforeMethod
    void setUp() {
        uniqueNumbers.clear();
        final File path = new File(FILE_NAME);
        if(!path.exists()) return;
        try (InputStream reader = FileUtils.openInputStream(path)){
            cachedData.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    void tearDown() {
        final File path = new File(FILE_NAME);
        try (OutputStream writer = FileUtils.openOutputStream(path)){
            cachedData.store(writer, COMMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @org.testng.annotations.Test
    public void lineToAngle() {
//        regressionTest(Tools.lineToAngle(new Point(10,-11), 100, (float)3.3), 1);
//        regressionTest(Tools.lineToAngle(new Point(1,8), 1, 3), 2);

    }


    private <T> void regressionTest(T object, Integer uniqueNumber) {
        //Check for duplicate uniqueNumber
        if (!uniqueNumbers.add(uniqueNumber)) throw new IllegalArgumentException("uniqueNumber is already used");

        //cache key is made from testMethodName + uniqueNumber (its hack)
        String testMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        //using ArrayUtils.toString for converting Template arguments, which could be array of primitives etc..
        String valueAsString = ArrayUtils.toString(object);
        String key = testMethodName + "_" + uniqueNumber.toString();

        String cachedValue = getCachedValue(key, valueAsString);
        if (cachedValue == null) {
            return;
        }
        Assert.assertEquals(String.format("RegressionTest uniqueNumber - %d", uniqueNumber), cachedValue, valueAsString);
    }

    //TODO remove helper methods toString?
    private <T> String toString(T object) {
        return object.toString();
    }
    private <T> String toString(T[] object) {
        return Arrays.deepToString(object);
    }
    private <T> String toString(int[] object) {
        return Arrays.toString(object);
    }
    private <T> String toString(double[] object) {
        return Arrays.toString(object);
    }

    private <R,T>  String getCachedValue(String key, String computedResult) {
        String value = cachedData.getProperty(key);
        if (value == null) cachedData.setProperty(key, computedResult);
        return value;
    }

}