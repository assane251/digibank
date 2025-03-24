package gm.rahmanproperties.optibank.mappers;

import gm.rahmanproperties.optibank.domain.Compte;
import gm.rahmanproperties.optibank.dtos.CompteDto;
import gm.rahmanproperties.optibank.enums.Type;

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
