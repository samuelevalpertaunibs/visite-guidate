package com.unibs.models;

import java.util.function.Consumer;

public record MenuOption(String label, Consumer<Void> action) {
}
