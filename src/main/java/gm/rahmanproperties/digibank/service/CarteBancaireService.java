package gm.rahmanproperties.digibank.service;

import gm.rahmanproperties.digibank.domain.CarteBancaire;
import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.enums.StatutCarte;
import gm.rahmanproperties.digibank.repository.CarteBancaireRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CarteBancaireService {
    private final CarteBancaireRepository carteRepository = new CarteBancaireRepository();

    public CarteBancaire demanderCarte(Compte compte) {
        String numero = genererNumeroCarte();
        String cvv = genererCVV();
        String pin = genererPin();
        CarteBancaire carte = CarteBancaire.builder()
                .numero(numero)
                .cvv(cvv)
                .dateExpiration(LocalDate.now().plusYears(3))
                .statut(StatutCarte.ACTIVE)
                .codePin(pin)
                .compte(compte)
                .build();
        return carteRepository.save(carte);
    }

    public void bloquerCarte(UUID carteId) {
        CarteBancaire carte = carteRepository.findById(carteId);
        if (carte != null) {
            carte.setStatut(StatutCarte.BLOQUEE);
            carteRepository.update(carte);
        }
    }

    public void debloquerCarte(UUID carteId) {
        CarteBancaire carte = carteRepository.findById(carteId);
        if (carte != null) {
            carte.setStatut(StatutCarte.ACTIVE);
            carteRepository.update(carte);
        }
    }

    public List<CarteBancaire> getCartesByCompte(UUID compteId) {
        return carteRepository.findByCompteId(compteId);
    }

    private String genererNumeroCarte() {
        Random rand = new Random();
        return String.format("%04d-%04d-%04d-%04d",
                rand.nextInt(10000), rand.nextInt(10000),
                rand.nextInt(10000), rand.nextInt(10000));
    }

    private String genererCVV() {
        return String.format("%03d", new Random().nextInt(1000));
    }

    private String genererPin() {
        return String.format("%04d", new Random().nextInt(10000));
    }

//    public CarteBancaire getCarteBancaireJoinCompte(UUID carteId) {
//        return carteRepository.findByCarteBancaireJoinCompte(carteId);
//    }
}