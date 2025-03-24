package gm.rahmanproperties.optibank.domain;

import gm.rahmanproperties.optibank.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotEmpty
    private String nom;
    @NotEmpty
    @Column(nullable = false, length = 100)
    private String prenom;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 300)
    private String password;
    @Column(unique = true)
    private String telephone;
    private LocalDate dateInscription;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "client")
    private List<Compte> comptes;

    @OneToMany(mappedBy = "client")
    private List<Credit> credits;

    @OneToMany(mappedBy = "client")
    private List<TicketSupport> tickets;
    private boolean firstLogin;
}
