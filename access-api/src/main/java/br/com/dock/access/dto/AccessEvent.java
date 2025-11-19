package br.com.dock.access.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessEvent {
    private UUID requestId;
    private Integer clientId;
    private String clientName;
    private Instant timestamp;

    private GeoLocation geolocation;
    private DeviceInfo device;
}
