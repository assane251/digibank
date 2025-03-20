package gm.rahmanproperties.digibank.mappers;

import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.dtos.CompteDto;
import gm.rahmanproperties.digibank.enums.Type;

public class CompteMapper {
    private CompteDto toDTO(Compte compte) {
        return CompteDto.builder()
                .id(compte.getId())
                .numero(compte.getNumero())
                .type(compte.getType() != null ? Type.valueOf(compte.getType().name()) : null)
                .solde(compte.getSolde())
                .dateCreation(compte.getDateCreation())
                .build();

    }

    private Compte toEntity(CompteDto dto) {
        return Compte.builder()
                .id(dto.getId())
                .numero(dto.getNumero())
                .type(dto.getType())
                .solde(dto.getSolde())
                .dateCreation(dto.getDateCreation())
                .build();
    }
}
