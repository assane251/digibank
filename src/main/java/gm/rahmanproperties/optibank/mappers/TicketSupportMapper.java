package gm.rahmanproperties.optibank.mappers;

import gm.rahmanproperties.optibank.domain.Admin;
import gm.rahmanproperties.optibank.domain.Client;
import gm.rahmanproperties.optibank.domain.TicketSupport;
import gm.rahmanproperties.optibank.dtos.TicketSupportDto;
import gm.rahmanproperties.optibank.enums.Status;

public class TicketSupportMapper {

    private TicketSupportDto toDTO(TicketSupport ticket) {
        return TicketSupportDto.builder()
                .id(ticket.getId())
                .sujet(ticket.getSujet())
                .description(ticket.getDescription())
                .dateOuverture(ticket.getDateOuverture())
                .statut(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .clientId(ticket.getClient().getId() != null ? ticket.getClient().getId() : null)
                .adminId(ticket.getAdmin().getId() != null ? ticket.getAdmin().getId() : null)
                .build();
    }

    private TicketSupport toEntity(TicketSupportDto dto) {
        return TicketSupport.builder()
                .id(dto.getId())
                .sujet(dto.getSujet())
                .description(dto.getDescription())
                .dateOuverture(dto.getDateOuverture())
                .status(dto.getStatut() != null ? Status.valueOf(dto.getStatut()) : null)
                .client(dto.getClientId() != null ? Client.builder().id(dto.getClientId()).build() : null)
                .admin(dto.getAdminId() != null ? Admin.builder().id(dto.getAdminId()).build() : null)
                .build();
    }
}
