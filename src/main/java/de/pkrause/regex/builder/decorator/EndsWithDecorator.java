package de.pkrause.regex.builder.decorator;

import de.pkrause.regex.builder.component.RegexComponent;

public class EndsWithDecorator extends RegexDecorator {
    private final String literal;

    public EndsWithDecorator(RegexComponent component, String literal) {
        super(component);
        this.literal = literal;
    }

    @Override
    public String build() {
        return component.build() + literal + "$";
    }
}
