package com.kdt.trafficlight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SampleTest {

    @Test
    @DisplayName("값 비교 예시")
    void equalsExample() {
        assertThat("RED".toLowerCase()).isEqualTo("red");
    }

    @Test
    @DisplayName("예외 검증 예시")
    void exceptionExample() {
        assertThatThrownBy(() -> Integer.parseInt("-"))
                .isInstanceOf(NumberFormatException.class);
    }
}
