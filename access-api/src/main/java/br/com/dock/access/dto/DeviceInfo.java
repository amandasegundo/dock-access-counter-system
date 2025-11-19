package br.com.dock.access.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String type;
    private String os;
    private String version;
    private String ipAddress;
}
