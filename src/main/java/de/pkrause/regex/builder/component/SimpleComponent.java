package de.pkrause.regex.builder.component;

public class SimpleComponent implements RegexComponent {
    private final String regex;

    public SimpleComponent(String regex) {
        this.regex = regex;
    }

    @Override
    public String build() {
        return regex;
    }
}
