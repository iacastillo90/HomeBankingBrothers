package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.TypeAccounts;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

    @RequestMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClientsDTO ();
    }
    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.getClientDTO(id);
    }
    @RequestMapping("/clients/current")
    public ClientDTO getClient(Authentication authentication) {
        return clientService.getClientDTOByEmailCurrent(authentication.getName());
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password) {

        String mensaje = " ";
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mensaje = "Missing data";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        if (clientService.findByEmail(email) != null) {
            mensaje = "Email already in use";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        Client client= new Client(firstName,lastName,email,passwordEncoder.encode(password));
        clientService.saveClient(client);
        String accountNumber = "VIN" + getStringRandomClient();
        Account newAccount = new Account(accountNumber, LocalDate.now(), 0.00, TypeAccounts.SAVINGS);
        client.addAccount(newAccount);
        accountService.saveAccount(newAccount);

        mensaje = "Cliente Creado";
        return new ResponseEntity<>(mensaje,HttpStatus.CREATED);
    }

    int min =00000000;
    int max =99999999;
    public int getRandomNumber(int min,int max){return (int) ((Math.random() * (max - min)) + min);}
    public String getStringRandomClient() {
        int randomNumber = getRandomNumber(min, max);
        return String.valueOf(randomNumber);
    }
}

