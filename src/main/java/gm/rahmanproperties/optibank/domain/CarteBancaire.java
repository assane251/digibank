package gm.rahmanproperties.optibank.domain;

import gm.rahmanproperties.optibank.enums.StatutCarte;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CarteBancaire")
@Builder
public class CarteBancaire {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String numero;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private LocalDate dateExpiration;

    @Column(precision = 15, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCarte statut = StatutCarte.EN_ATTENTE;

    @Column(nullable = false, length = 4)
    private String codePin;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte compte;

    @Override
    public String toString() {
        return "CarteBancaire{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", cvv='" + cvv + '\'' +
                ", dateExpiration='" + dateExpiration + '\'' +
                ", statut='" + statut + '\'' +
                ", codePin='" + codePin + '\'' +
                ", compte=" + compte +
                '}';
    }
}
