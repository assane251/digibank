package gm.rahmanproperties.digibank.mappers;

import gm.rahmanproperties.digibank.domain.Admin;
import gm.rahmanproperties.digibank.domain.TicketSupport;
import gm.rahmanproperties.digibank.dtos.AdminDto;

import java.util.stream.Collectors;

public class AdminMapper {

    public static AdminDto toDto(Admin admin) {
        if (admin == null) {
            return null;
        }
        return AdminDto.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .password(admin.getPassword())
                .role(admin.getRole())
                .firstLogin(admin.isFirstLogin())
                .tickets(admin.getTickets().stream().map(TicketSupport::getId).collect(Collectors.toList()))
                .build();
    }

    public static Admin toEntity(AdminDto adminDto) {
        if (adminDto == null) {
            return null;
        }
        return Admin.builder()
                .id(adminDto.getId())
                .username(adminDto.getUsername())
                .password(adminDto.getPassword())
                .role(adminDto.getRole())
                .build();
    }
}