package de.pkrause.regex.builder.decorator;

import de.pkrause.regex.builder.component.RegexComponent;

public class StartsWithDecorator extends RegexDecorator {
    private final String literal;

    public StartsWithDecorator(RegexComponent component, String literal) {
        super(component);
        this.literal = literal;
    }

    @Override
    public String build() {
        return "^" + literal + component.build();
    }
}
