package gm.rahmanproperties.digibank.service;

import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.dtos.ClientDto;
import gm.rahmanproperties.digibank.enums.Status;
import gm.rahmanproperties.digibank.enums.Type;
import gm.rahmanproperties.digibank.mappers.ClientMapper;
import gm.rahmanproperties.digibank.repository.ClientRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository;
    private final CompteService compteService;

    public ClientService() {
        this.clientRepository = new ClientRepository();
        this.compteService = new CompteService();
    }

    // Create a new client and account with user-provided type and balance
    public Client creerClient(ClientDto clientDto, Type typeCompte, BigDecimal soldeInitial) {
        if (clientRepository.existsByEmail(clientDto.getEmail()) != null) {
            throw new IllegalArgumentException("Un client avec cet email existe déjà.");
        }

        String hashedPassword = BCrypt.hashpw(clientDto.getPassword(), BCrypt.gensalt());
        ClientDto newClientDto = ClientDto.builder()
                .email(clientDto.getEmail())
                .telephone(clientDto.getTelephone())
                .nom(clientDto.getNom())
                .prenom(clientDto.getPrenom())
                .status(clientDto.getStatus() != null ? clientDto.getStatus() : Status.EN_ATTENTE)
                .dateInscription(LocalDate.now())
                .password(hashedPassword)
                .build();

        Client client = ClientMapper.toEntity(newClientDto);
        clientRepository.save(client);

        // Create account with user-provided type and balance
        compteService.creerCompte(client, typeCompte, soldeInitial);
        return client;
    }

    public Client saveClient(ClientDto clientDto) {
        Client client = ClientMapper.toEntity(clientDto);
        clientRepository.save(client);
        return client;
    }

    public void activeStatus(String email) {
        Client client = clientRepository.existsByEmail(email);
        if (client == null) {
            throw new IllegalArgumentException("Client introuvable avec cet email: " + email);
        }
        client.setStatus(Status.ACTIF);
        clientRepository.update(client);
    }

    // Other methods remain unchanged
    public Client authentificationClient(String email) {
        ClientDto clientDto = clientRepository.authenticate(email);
        return clientDto != null ? ClientMapper.toEntity(clientDto) : null;
    }

    public List<Client> listerClients() {
        return clientRepository.findAll();
    }
    public List<Client> listerClientsParStatus(Status status) {
        return clientRepository.findByStatus(status);
    }

    public void miseAJourClient(ClientDto clientDto) {
        Client existingClient = clientRepository.existsByEmail(clientDto.getEmail());
        if (existingClient == null) {
            throw new IllegalArgumentException("Client introuvable avec cet email: " + clientDto.getEmail());
        }
        Client updatedClient = ClientMapper.toEntity(clientDto);
        updatedClient.setId(existingClient.getId());
        clientRepository.update(updatedClient);
    }

    public void toggleClientStatus(String email, Status newStatus) {
        Client client = clientRepository.existsByEmail(email);
        if (client == null) {
            throw new IllegalArgumentException("Client introuvable avec cet email: " + email);
        }
        client.setStatus(newStatus);
        clientRepository.update(client);
    }

    public void updateClientPassword(String username, String password) {
        Client client = authentificationClient(username);
        if (client == null) {
            throw new IllegalArgumentException("Admin not found for username: " + username);
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        client.setPassword(hashedPassword);
        client.setFirstLogin(false);
        clientRepository.update(client);
    }

    public Client existByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }
}