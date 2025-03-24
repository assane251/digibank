package gm.rahmanproperties.optibank.dtos;

import gm.rahmanproperties.optibank.enums.Type;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompteDto {
    private UUID id;
    private String numero;
    private Type type;
    private BigDecimal solde;
    private LocalDate dateCreation;
    private UUID client;
    private List<UUID> transactionsSource;
    private List<UUID> transactionsDest;
    private List<UUID> cartes;
    private List<UUID> frais;
}
