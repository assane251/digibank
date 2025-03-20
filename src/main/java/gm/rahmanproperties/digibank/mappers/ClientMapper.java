package gm.rahmanproperties.digibank.mappers;

import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.domain.Credit;
import gm.rahmanproperties.digibank.domain.TicketSupport;
import gm.rahmanproperties.digibank.dtos.ClientDto;  // Changed from AdminDto
import gm.rahmanproperties.digibank.enums.Status;

import java.util.UUID;
import java.util.stream.Collectors;

public class ClientMapper {

    public static ClientDto toDto(Client client) {  // Changed return type to ClientDto
        if (client == null) {
            return null;
        }
        return ClientDto.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .email(client.getEmail())
                .password(client.getPassword())
                .telephone(client.getTelephone())
                .dateInscription(client.getDateInscription())
                .status(client.getStatus())
                .credits(client.getCredits() != null ?
                        client.getCredits().stream()
                                .map(Credit::getId)
                                .map(UUID::fromString)
                                .collect(Collectors.toList())
                        : null)
                .comptes(client.getComptes() != null ?
                        client.getComptes().stream()
                                .map(Compte::getId)
                                .collect(Collectors.toList())
                        : null)
                .tickets(client.getTickets() != null ?
                        client.getTickets().stream()
                                .map(TicketSupport::getId)
                                .map(UUID::fromString)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static Client toEntity(ClientDto clientDto) {
        if (clientDto == null) {
            return null;
        }
        return Client.builder()
                .id(clientDto.getId())
                .nom(clientDto.getNom())           // Added missing field
                .prenom(clientDto.getPrenom())     // Added missing field
                .email(clientDto.getEmail())
                .password(clientDto.getPassword())
                .telephone(clientDto.getTelephone())
                .dateInscription(clientDto.getDateInscription())
                .status(clientDto.getStatus())     // Use DTO's status instead of hardcoded ACTIF
                .build();
    }
}