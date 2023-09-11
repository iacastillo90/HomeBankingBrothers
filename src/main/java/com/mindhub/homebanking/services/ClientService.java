package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getClientsDTO();
    ClientDTO getClientDTO (long id);
    ClientDTO getClientDTOByEmailCurrent(String email);
    Client findById (long id);
    Client findByEmail (String email);
    void saveClient(Client client);
}
