package br.com.dock.access.helper;

import java.time.Instant;

public class InstantHelper {

    public static Instant parseInstant(String epochSeconds){
        return Instant.ofEpochSecond(Long.parseLong(epochSeconds));
    }
}
