package com.kdt.rps.api;

public enum Hand {
    PAPER,
    ROCK,
    SCISSORS;

    public String getAgainst() {
            return switch (this) {
                    case PAPER -> "ROCK";
                    case SCISSORS -> "PAPER";
                    default -> "SCISSORS";
            };
    }

    public boolean beats(Hand other) {
            return getAgainst().equals(other.name());
    }
}
