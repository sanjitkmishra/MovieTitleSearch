import com.mts.util.TitleSearch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TitleSearchTest {

    private TitleSearch unit;

    @Before
    public void setUp() throws IOException {
        unit = new TitleSearch();
        unit.buildIndex("resource/movies.txt");
    }

    @Test
    public void testTitleSearchWithNullOrEmptyValue() {
        List<String> foundTitles1 = unit.find(null);
        Assert.assertEquals(0, foundTitles1.size());

        List<String> foundTitles2 = unit.find("");
        Assert.assertEquals(0, foundTitles2.size());
    }

    @Test
    public void testTitleSearchWithValidString() {
        String phrase = "Shichinin no samurai";
        List<String> foundTitles = unit.find(phrase);
        Assert.assertEquals(1, foundTitles.size());
        Assert.assertEquals("Shichinin no samurai (1954)", foundTitles.get(0));
    }

    @Test
    public void testTitleSearchWithInvalidString() {
        String phrase = "hichinin o amurai";
        List<String> foundTitles = unit.find(phrase);
        Assert.assertEquals(0, foundTitles.size());
    }

    @Test
    public void testTitleSearchWithValidStringAlphaNumeric() {
        String phrase = "8½ (1963)";
        List<String> foundTitles = unit.find(phrase);
        Assert.assertEquals(1, foundTitles.size());
        Assert.assertEquals("8½ (1963)", foundTitles.get(0));
    }

    @Test
    public void testTitleSearchWithValidStringWithMultipleResult() {
        String phrase = "Star Trek";
        List<String> foundTitles = unit.find(phrase);
        Assert.assertEquals(6, foundTitles.size());
        Assert.assertEquals("Star Trek (2009)", foundTitles.get(0));
        Assert.assertEquals("Star Trek 1 (2009)", foundTitles.get(1));
        Assert.assertEquals("Star Trek 2 (2009)", foundTitles.get(2));
        Assert.assertEquals("Star Trek 3 (2009)", foundTitles.get(3));
        Assert.assertEquals("Star Wars (1977)", foundTitles.get(4));
        Assert.assertEquals("Star Trek 4 (2009)", foundTitles.get(5));
    }

    @Test
    public void testTitleSearchWithValidStringNotMatchingResult() {
        String phrase = "Shichinin";
        List<String> foundTitles = unit.find(phrase);
        Assert.assertEquals(0, foundTitles.size());
    }
}
