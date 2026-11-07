package com.platform.familytree.shared.ids;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Uuid7Test {

    @Test
    void generatesVersion7AndRfcVariant() {
        UUID id = Uuid7.generate();
        assertThat(id.version()).isEqualTo(7);
        assertThat(id.variant()).isEqualTo(2);
    }

    @Test
    void idsSortInCreationOrder() {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            ids.add(Uuid7.generate());
        }
        List<String> sorted = ids.stream().map(UUID::toString).sorted().toList();
        List<String> generated = ids.stream().map(UUID::toString).toList();
        assertThat(generated).isEqualTo(sorted);
    }

    @Test
    void sameMillisecondIdsAreUnique() {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            ids.add(Uuid7.generate());
        }
        assertThat(ids).doesNotHaveDuplicates();
    }
}
