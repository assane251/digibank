package gm.rahmanproperties.optibank.service;

import gm.rahmanproperties.optibank.domain.Client;
import gm.rahmanproperties.optibank.domain.Credit;
import gm.rahmanproperties.optibank.domain.Remboursement;
import gm.rahmanproperties.optibank.enums.StatutCredit;
import gm.rahmanproperties.optibank.repository.CreditRepository;
import gm.rahmanproperties.optibank.repository.RemboursementRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreditService {
    private final CreditRepository creditRepository;
    private final RemboursementRepository remboursementRepository;

    public CreditService(CreditRepository creditRepository, RemboursementRepository remboursementRepository) {
        this.creditRepository = creditRepository;
        this.remboursementRepository = remboursementRepository;
    }

    public Credit demanderCredit(Client client, BigDecimal montant, double tauxInteret, int dureeMois) {
        BigDecimal mensualite = calculerMensualite(montant, tauxInteret, dureeMois);
        Credit credit = Credit.builder()
                .montant(montant)
//                .tauxInteret(tauxInteret)
                .dureeMois(dureeMois)
                .mensualite(mensualite)
                .dateDemande(LocalDate.now())
                .statut(StatutCredit.EN_ATTENTE)
                .client(client)
                .build();
        return creditRepository.save(credit);
    }

    public void approuverCredit(UUID creditId) {
        Credit credit = creditRepository.findById(creditId);
        if (credit != null) {
            credit.setStatut(StatutCredit.APPROUVE);
            creditRepository.update(credit);
        }
    }

    public void effectuerRemboursement(UUID creditId, BigDecimal montant) {
        Credit credit = creditRepository.findById(creditId);
        if (credit == null || !"APPROUVE".equals(credit.getStatut())) {
            throw new IllegalStateException("Crédit non valide pour remboursement");
        }
        Remboursement remboursement = Remboursement.builder()
                .montant(montant)
                .date(LocalDate.now())
                .credit(credit)
                .build();
        remboursementRepository.save(remboursement);

        List<Remboursement> remboursements = remboursementRepository.findByCreditId(creditId);
        BigDecimal totalRembourse = remboursements.stream()
                .map(Remboursement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        if (totalRembourse.compareTo(credit.getMontant().multiply(BigDecimal.valueOf(1 + credit.getTauxInteret() / 100))) >= 0) {
//            credit.setStatut(StatutCredit.TERMINE);
//            creditRepository.update(credit);
//        }
    }

    public List<Credit> getCreditsByClient(UUID clientId) {
        return creditRepository.findByClientId(clientId);
    }

    public List<Remboursement> getEcheancier(UUID creditId) {
        return remboursementRepository.findByCreditId(creditId);
    }

    private BigDecimal calculerMensualite(BigDecimal montant, double tauxInteret, int dureeMois) {
        double tauxMensuel = tauxInteret / 100 / 12;
        double mensualite = montant.doubleValue() * tauxMensuel * Math.pow(1 + tauxMensuel, dureeMois)
                / (Math.pow(1 + tauxMensuel, dureeMois) - 1);
        return BigDecimal.valueOf(mensualite).setScale(2, RoundingMode.HALF_UP);
    }
}