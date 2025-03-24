package gm.rahmanproperties.optibank.dtos;

import lombok.*;

import java.util.Date;
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
