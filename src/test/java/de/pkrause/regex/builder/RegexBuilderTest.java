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
    public void build_withLiteral_withConsumer_shouldBuildCorrectRegex() {
        RegexBuilder regexBuilder = new RegexBuilder("");

        regexBuilder.withLiteral(innerBuilder -> innerBuilder.withGroup("abc").withOneOrMore());
        assertEquals("(abc)+", regexBuilder.build());

        regexBuilder.withLiteral(innerBuilder ->
                innerBuilder
                        .withGroup("def")
                        .withQuantifier(3)
                        .withGroup("ghi")
                        .withOneOrMore()
                        .build()
        );
        assertEquals("(abc)+(def){3}(ghi)+", regexBuilder.build());
    }

    @Test
    public void build_withQuantifier_shouldBuildCorrectRegex() {
        RegexBuilder regexBuilder = new RegexBuilder("");

        regexBuilder.withGroup("abc").withQuantifier(2, 4);
        assertEquals("(abc){2,4}", regexBuilder.build());

        try {
            regexBuilder.withLiteral("def").withQuantifier(4, 2);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }

        try {
            regexBuilder.withLiteral("ghi").withQuantifier(-1, 3);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }

        try {
            regexBuilder.withLiteral("jkl").withQuantifier(1, -3);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }

        try {
            regexBuilder.withLiteral("mno").withQuantifier(-2, -1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }
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
    public void build_withAlternatives_withConsumers_shouldBuildCorrectRegex() {
        // Test case 1: Valid alternatives
        RegexBuilder regexBuilder1 = new RegexBuilder("");
        regexBuilder1.withAlternatives(
                rb -> rb.withGroup("abc").withOneOrMore(),
                rb -> rb.withGroup("def").withOneOrMore()
        );
        assertEquals("((abc)+|(def)+)", regexBuilder1.build());

        // Test case 2: Valid alternatives with quantifiers
        RegexBuilder regexBuilder2 = new RegexBuilder("");
        regexBuilder2.withAlternatives(
                rb -> rb.withGroup("123").withQuantifier(2),
                rb -> rb.withGroup("456").withQuantifier(3)
        );
        assertEquals("((123){2}|(456){3})", regexBuilder2.build());

        // Test case 3: Exception thrown during alternative construction
        try {
            RegexBuilder regexBuilder3 = new RegexBuilder("");
            regexBuilder3.withAlternatives(
                    rb -> rb.withLiteral("ghi"),
                    rb -> { throw new RuntimeException("Simulated exception"); }
            );
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Error constructing alternatives: Simulated exception", e.getMessage());
        }
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

        String expectedRegex = "(?i)^startabc[0-9]{3}[A-z]{6}+end$";

        // Construct the regex using the RegexBuilder
        String actualRegex = new RegexBuilder("abc")
                .startsWith("start")
                .withLiteral(rb -> rb
                        .withRange(0, 9).withQuantifier(3)
                        .withRange('A', 'z').withQuantifier(6)
                        .withOneOrMore()
                        .build())
                .endsWith("end")
                .ignoreCase()
                .build();

        assertEquals(expectedRegex, actualRegex);

        // Test if the constructed regex matches specific input strings
        assertTrue("startabc123defghiend".matches(actualRegex));
        assertTrue("startABC123DEFGHIEND".matches(actualRegex)); // This should match
        assertFalse("startXYZ789XYZend".matches(actualRegex));   // This should not match
    }

}
