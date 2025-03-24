package gm.rahmanproperties.optibank.dtos;

import gm.rahmanproperties.optibank.enums.Status;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    private UUID id;
    @NotEmpty
    private String nom;
    @NotEmpty
    private String prenom;
    private String email;
    private String password;
    private String telephone;
    private LocalDate dateInscription;
    private Status status;
    private List<UUID> comptes;
    private List<UUID> credits;
    private List<UUID> tickets;
    private boolean firstLogin;
}
