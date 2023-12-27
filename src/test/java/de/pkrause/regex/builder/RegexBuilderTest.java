package de.pkrause.regex.builder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegexBuilderTest {

    @Test
    void build_withLiteral_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withLiteral("123")
                .build();

        assertEquals("abc123", regex);
    }

    @Test
    void build_withCharacterClass_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withCharacterClass("0-9")
                .build();

        assertEquals("abc[0-9]", regex);
    }

    @Test
    void build_withZeroOrMore_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withZeroOrMore()
                .build();

        assertEquals("abc*", regex);
    }

    @Test
    void build_withOneOrMore_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withOneOrMore()
                .build();

        assertEquals("abc+", regex);
    }

    @Test
    void build_withOptional_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withOptional()
                .build();

        assertEquals("abc?", regex);
    }

    @Test
    void build_withGroup_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withGroup("123")
                .build();

        assertEquals("abc(123)", regex);
    }

    @Test
    void build_withRangeChar_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withRange('0', '9')
                .build();

        assertEquals("abc[0-9]", regex);
    }

    @Test
    void build_withRangeInt_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withRange(0, 9)
                .build();

        assertEquals("abc[0-9]", regex);
    }

    @Test
    void build_withAlternatives_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withAlternatives( "ABC", "DEF")
                .build();

        assertEquals("abc(ABC|DEF)", regex);
    }

    @Test
    void build_withAlternatives_complex_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .withAlternatives(true, "[0-9]+", "[A-Z]+")
                .build();

        assertEquals("abc([0-9]+|[A-Z]+)", regex);
    }

    @Test
    void build_ignoreCase_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .ignoreCase()
                .build();

        assertEquals("(?i)abc", regex);
    }

    @Test
    void build_startsWith_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .startsWith("start")
                .build();

        assertEquals("^startabc", regex);
    }

    @Test
    void build_endsWith_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .endsWith("end")
                .build();

        assertEquals("abcend$", regex);
    }

    @Test
    void build_completeExample_shouldBuildCorrectRegex() {
        String regex = new RegexBuilder("abc")
                .startsWith("start")
                .withAlternatives(true, "[0-9]+", "[A-z]+")
                .withOneOrMore()
                .endsWith("end")
                .ignoreCase()
                .build();

        assertEquals("(?i)^startabc([0-9]+|[A-z]+)+end$", regex);
    }
}
