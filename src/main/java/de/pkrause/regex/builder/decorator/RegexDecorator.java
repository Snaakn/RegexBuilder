package de.pkrause.regex.builder.decorator;

import de.pkrause.regex.builder.component.RegexComponent;

public abstract class RegexDecorator implements RegexComponent {
    protected final RegexComponent component;

    public RegexDecorator(RegexComponent component) {
        this.component = component;
    }
}
