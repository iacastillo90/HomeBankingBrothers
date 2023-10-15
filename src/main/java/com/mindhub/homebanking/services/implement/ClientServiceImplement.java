package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ClientServiceImplement implements ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Override
    public List<ClientDTO> getClientsDTO() {
        return clientRepository.findAll()
                .stream()
                .map(pepe -> new ClientDTO(pepe))
                .collect(Collectors.toList());
    }
    @Override
    public ClientDTO getClientDTO(long id) {
        return new ClientDTO(this.findById(id));
    }
    @Override
    public ClientDTO getClientDTOByEmailCurrent(String authentication) {
        return new ClientDTO(this.findByEmail(authentication));
    }

    @Override
    public Client findById(long id) {
        return clientRepository.findById(id).orElse(null);
    }
    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public void saveClient(Client client) {
        clientRepository.save(client);
    }
}
