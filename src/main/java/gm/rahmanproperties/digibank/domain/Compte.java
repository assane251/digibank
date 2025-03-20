package gm.rahmanproperties.digibank.domain;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import gm.rahmanproperties.digibank.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "compte")
public class Compte extends RecursiveTreeObject<Compte> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "numero ne devrais pas etre null")
    @Size(max = 100)
    @Column(unique = true, nullable = false, length = 20)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(precision = 15, scale = 2)
    private BigDecimal solde;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "compteSource")
    private List<Transaction> transactionsSource;

    @OneToMany(mappedBy = "compteDest")
    private List<Transaction> transactionsDest;

    @OneToMany(mappedBy = "compte")
    private List<CarteBancaire> cartes;

    @OneToMany(mappedBy = "compte")
    private List<FraisBancaire> frais;
}
