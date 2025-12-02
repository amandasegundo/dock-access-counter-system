package br.com.dock.access.mapper;

import br.com.dock.access.AddAccessRequest;
import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.DeviceInfo;
import br.com.dock.access.dto.GeoLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AccessEventMapper {

    AccessEventMapper INSTANCE = Mappers.getMapper(AccessEventMapper.class);

    @Mapping(target = "requestId", source = "requestId", qualifiedByName = "stringToUuid")
    @Mapping(target = "geolocation", source = "geolocation")
    @Mapping(target = "device", source = "device")
    AccessEventMessage fromProto(AddAccessRequest proto);

    GeoLocation map(br.com.dock.access.Geolocation g);

    DeviceInfo map(br.com.dock.access.Device d);

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return UUID.fromString(id);
    }
}
