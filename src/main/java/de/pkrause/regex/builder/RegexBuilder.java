package de.pkrause.regex.builder;

import de.pkrause.regex.builder.component.RegexComponent;
import de.pkrause.regex.builder.component.SimpleComponent;
import de.pkrause.regex.builder.decorator.AlternativeDecorator;
import de.pkrause.regex.builder.decorator.EndsWithDecorator;
import de.pkrause.regex.builder.decorator.StartsWithDecorator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegexBuilder {
    protected RegexComponent root;

    private String escapeRegex(String regex, boolean isComplexRegex) {

        if (isComplexRegex) return regex;

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

    public RegexBuilder withLiteral(Consumer<RegexBuilder> regexBuilderConsumer) {
        RegexBuilder regexBuilder = acceptAndReturnRegexBuilder(regexBuilderConsumer);
        this.root = new SimpleComponent(root.build() + regexBuilder.build());
        return this;
    }

    private static RegexBuilder acceptAndReturnRegexBuilder(Consumer<RegexBuilder> regexBuilderConsumer) {
        if (regexBuilderConsumer == null) {
            throw new IllegalArgumentException("Unexpected consumer type");
        }

        RegexBuilder regexBuilder = new RegexBuilder("");
        regexBuilderConsumer.accept(regexBuilder);
        return regexBuilder;
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

    public RegexBuilder withQuantifier(int min, int max) {
        if (min < 0 || max < 0 || max < min) {
            throw new IllegalArgumentException("Invalid quantifier range");
        }
        this.root = new SimpleComponent(root.build() + "{" + min + "," + max + "}");
        return this;
    }

    public RegexBuilder withQuantifier(int quantifier) {
        if (quantifier <= 0) {
            throw new IllegalArgumentException("Invalid quantifier range");
        }
        this.root = new SimpleComponent(root.build() + "{" + quantifier + "}");
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


    @SafeVarargs
    public final RegexBuilder withAlternatives(Consumer<RegexBuilder>... alternativeConsumers) {
        try {


            String[] alternatives = Arrays.stream(alternativeConsumers)
                    .map(RegexBuilder::acceptAndReturnRegexBuilder)
                    .map(RegexBuilder::build)
                    .toList()
                    .toArray(new String[alternativeConsumers.length]);

            return withAlternatives(true, alternatives);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error constructing alternatives: " + e.getMessage(), e);
        }
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

}
