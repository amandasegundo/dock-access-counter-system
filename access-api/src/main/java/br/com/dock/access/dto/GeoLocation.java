package br.com.dock.access.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {

    private double latitude;
    private double longitude;
}
