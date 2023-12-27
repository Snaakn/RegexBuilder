package de.pkrause.regex.builder.decorator;

import de.pkrause.regex.builder.component.RegexComponent;

import java.util.ArrayList;
import java.util.List;

public class AlternativeDecorator extends RegexDecorator {
    private final List<RegexComponent> alternatives;

    public AlternativeDecorator(RegexComponent component, List<RegexComponent> alternatives) {
        super(component);
        this.alternatives = new ArrayList<>(alternatives);
    }

    @Override
    public String build() {
        StringBuilder alternativeRegex = new StringBuilder();
        for (RegexComponent alt : alternatives) {
            alternativeRegex.append(alt.build()).append("|");
        }
        alternativeRegex.deleteCharAt(alternativeRegex.length() - 1); // Remove the last "|"
        return component.build() + "(" + alternativeRegex.toString() + ")";
    }
}