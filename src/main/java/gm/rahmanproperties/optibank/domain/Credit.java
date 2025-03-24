package gm.rahmanproperties.optibank.domain;

import gm.rahmanproperties.optibank.enums.StatutCredit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "credit")
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxInteret;

    @Column(nullable = false)
    private int dureeMois;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mensualite;

    @Column(nullable = false)
    private LocalDate dateDemande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCredit statut = StatutCredit.EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "credit")
    private List<Remboursement> remboursements;
}
