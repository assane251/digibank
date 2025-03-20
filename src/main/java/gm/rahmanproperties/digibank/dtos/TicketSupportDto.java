package gm.rahmanproperties.digibank.dtos;

import gm.rahmanproperties.digibank.enums.Type;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketSupportDto {
    private String id;
    private String sujet;
    private String description;
    private Date dateOuverture;
    private String statut;
    private UUID clientId;
    private Long adminId;
}
