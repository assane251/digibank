package gm.rahmanproperties.digibank.domain;

import gm.rahmanproperties.digibank.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "ticketSupport")
public class TicketSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String sujet;

    @Column(nullable = false, length = 300)
    private String description;

    private Date dateOuverture;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
