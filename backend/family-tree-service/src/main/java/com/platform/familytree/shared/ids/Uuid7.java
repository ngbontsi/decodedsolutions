package com.platform.familytree.shared.ids;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RFC 9562 UUIDv7: 48-bit millisecond timestamp, 4 version bits, 12-bit counter,
 * 2 variant bits, 62 random bits.
 *
 * <p>Time-ordered keys keep B-tree inserts appending instead of scattering across
 * random pages. The counter guarantees strict ordering inside one millisecond.
 */
public final class Uuid7 {

    private static final AtomicLong STATE = new AtomicLong();

    private Uuid7() {
    }

    public static UUID generate() {
        long ts = System.currentTimeMillis();

        long packed = STATE.updateAndGet(prev -> {
            long prevTs = prev >>> 12;
            long counter = prev & 0xFFFL;
            if (ts > prevTs) {
                return ts << 12;
            }
            if (counter < 0xFFFL) {
                return prev + 1;
            }
            throw new IllegalStateException("UUIDv7 counter exhausted within one millisecond");
        });

        long ms = packed >>> 12;
        long counter = packed & 0xFFFL;

        long msb = ((ms & 0xFFFF_FFFF_FFFFL) << 16) | 0x7000L | counter;
        long lsb = 0x8000_0000_0000_0000L
                | (ThreadLocalRandom.current().nextLong() & 0x3FFF_FFFF_FFFF_FFFFL);

        return new UUID(msb, lsb);
    }
}
