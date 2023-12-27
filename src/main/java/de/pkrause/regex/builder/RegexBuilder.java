package de.pkrause.regex.builder;

import de.pkrause.regex.builder.component.RegexComponent;
import de.pkrause.regex.builder.component.SimpleComponent;
import de.pkrause.regex.builder.decorator.AlternativeDecorator;
import de.pkrause.regex.builder.decorator.EndsWithDecorator;
import de.pkrause.regex.builder.decorator.StartsWithDecorator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegexBuilder {
    protected RegexComponent root;

    private String escapeRegex(String regex, boolean isComplexRegex) {

        if(isComplexRegex) return regex;

        String specialChars = "\\.*+?|()[]{}^$";
        for (char specialChar : specialChars.toCharArray()) {
            regex = regex.replace(String.valueOf(specialChar), "\\" + specialChar);
        }
        return regex;
    }

    public RegexBuilder(String literal) {
        this.root = new SimpleComponent(literal);
    }

    public RegexBuilder withLiteral(String literal) {
        return withLiteral(literal, false);
    }
    public RegexBuilder withLiteral(String literal, boolean isComplexRegex) {
        this.root = new SimpleComponent(root.build() + escapeRegex(literal, isComplexRegex));
        return this;
    }

    public RegexBuilder withCharacterClass(String charClass) {
        return withCharacterClass(charClass, false);
    }
    public RegexBuilder withCharacterClass(String charClass, boolean isComplexRegex) {
        this.root = new SimpleComponent(root.build() + "[" + escapeRegex(charClass, isComplexRegex) + "]");
        return this;
    }

    public RegexBuilder withZeroOrMore() {
        this.root = new SimpleComponent(root.build() + "*");
        return this;
    }

    public RegexBuilder withOneOrMore() {
        this.root = new SimpleComponent(root.build() + "+");
        return this;
    }

    public RegexBuilder withOptional() {
        this.root = new SimpleComponent(root.build() + "?");
        return this;
    }

    public RegexBuilder withGroup(String group) {
        return withGroup(group, false);
    }
    public RegexBuilder withGroup(String group, boolean isComplexRegex) {
        this.root = new SimpleComponent(root.build() + "(" + escapeRegex(group, isComplexRegex) + ")");
        return this;
    }

    public RegexBuilder withRange(char start, char end) {
        this.root = new SimpleComponent(root.build() + "[" + start + "-" + end + "]");
        return this;
    }


    public RegexBuilder withRange(int start, int end) {
        this.root = new SimpleComponent(root.build() + "[" + start + "-" + end + "]");
        return this;
    }

    public RegexBuilder withAlternatives(String... alternatives) {
        return withAlternatives(false, alternatives);
    }

    public RegexBuilder withAlternatives(boolean isComplexRegex, String... alternatives) {
        List<RegexComponent> components = Stream.of(alternatives)
                .map(literal -> escapeRegex(literal, isComplexRegex))
                .map(SimpleComponent::new)
                .collect(Collectors.toList());
        this.root = new AlternativeDecorator(root, components);
        return this;
    }

    public RegexBuilder ignoreCase() {
        this.root = new SimpleComponent("(?i)" + root.build());
        return this;
    }

    public final RegexBuilder startsWith(String literal) {
        return startsWith(literal, false);
    }

    public final RegexBuilder startsWith(String literal, boolean isComplexRegex) {
        this.root = new StartsWithDecorator(root, escapeRegex(literal, isComplexRegex));
        return this;
    }

    public final RegexBuilder endsWith(String literal) {
        return endsWith(literal, false);
    }

    public final RegexBuilder endsWith(String literal, boolean isComplexRegex) {
        this.root = new EndsWithDecorator(root, escapeRegex(literal, isComplexRegex));
        return this;
    }

    public String build() {
        return root.build();
    }

    public static void main(String[] args) {
        String regex = new RegexBuilder("abc")
                .startsWith("start")
                .withAlternatives(true, "[0-9]+", "[A-z]+")
                .withOneOrMore()
                .endsWith("end")
                .ignoreCase()
                .build();

        System.out.println("Built Regex: " + regex);

        // Now you can use the regex in your Java code
        if ("startabc123defghiend".matches(regex)) {
            System.out.println("Match!");
        } else {
            System.out.println("No match.");
        }
    }
}
