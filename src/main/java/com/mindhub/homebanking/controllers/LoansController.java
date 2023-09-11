package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LoansController {

    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/loans")
    public List<LoanDTO> getLoanApplicationDTO() {
        return loanService.getLoanApplicationDTO();
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<String> newLoans(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                           Authentication authentication) {

        // Obtener el cliente autenticado
        Client client = clientService.findByEmail(authentication.getName());
        String mensaje = "";

        // Validaciones de datos
        if (loanApplicationDTO == null) {
            mensaje = "Data is missing.";
            return new ResponseEntity<>(mensaje , HttpStatus.BAD_REQUEST);
        }

        if (loanApplicationDTO.getAmount() <= 0) {
            mensaje = "Loan amount must be greater than 0.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        if (loanApplicationDTO.getPayments().isEmpty() || loanApplicationDTO.getPayments().get(0) <= 0) {
            mensaje = "Number of payments must be greater than 0.";
            return new ResponseEntity<>( mensaje, HttpStatus.BAD_REQUEST);
        }

        if (loanApplicationDTO.getTypeName() == null) {
            mensaje = "Loan ID is missing.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        if (loanApplicationDTO.getNumberAccountDestination() == null) {
            mensaje = "Destination account number is missing.";
            return new ResponseEntity<>( mensaje, HttpStatus.BAD_REQUEST);
        }

        // Verificar que el préstamo existe
        Loan loan = loanService.findByName(LoanType.valueOf(loanApplicationDTO.getTypeName()));
        if (loanApplicationDTO == null || loanApplicationDTO.getTypeName() == null) {
            mensaje = "Loan ID is missing.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }
        if (loan == null) {
            mensaje = "Loan not found.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Verificar que el monto solicitado no supere el máximo permitido
        if (loanApplicationDTO.getAmount() > loan.getMaxAccount()) {
            mensaje = "Loan amount exceeds the maximum allowed.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Verificar que la cantidad de cuotas esté disponible
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments().get(0))) {
            mensaje = "Number of payments is not available for this loan.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Verificar que la cuenta de destino exista
        Account destinationAccount = accountService.findByNumber(loanApplicationDTO.getNumberAccountDestination());
        if (destinationAccount == null) {
            mensaje = "Destination account not found.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Verificar que la cuenta de destino pertenezca al cliente autenticado
        if (!client.getAccounts().contains(destinationAccount)) {
            mensaje = "Destination account does not belong to the authenticated client.";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        // Verificar la cantidad de préstamos actuales del cliente y su tipo
        long existingLoans = client.getClientLoans()
                .stream()
                .filter(clientLoan -> clientLoan.getLoan().equals(loan))
                .count();

        if (existingLoans >= 3) {
            mensaje = "You have reached the maximum number of loans of this type.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Calcular el monto del préstamo con el 20% de interés
        Double loanInterest = loanApplicationDTO.getAmount() * 1.20;

        // Crear solicitud de préstamo
        ClientLoan clientLoan = new ClientLoan(loanInterest, loanApplicationDTO.getPayments().get(0), client, loan);
        clientLoanService.saveClientLoan(clientLoan);

        // Crear transacción
        Transaction transactionLoanCredit = new Transaction(
                TransactionType.CREDIT,
                loanInterest,
                loan.getName() + " loan approved",
                LocalDateTime.now()
        );
        transactionLoanCredit.setAccount(destinationAccount);
        transactionService.saveTransaction(transactionLoanCredit);

        // Actualizar el balance de la cuenta de destino
        double newBalance = destinationAccount.getBalance() + loanInterest;
        destinationAccount.setBalance(newBalance);
        accountService.saveAccount(destinationAccount);

        mensaje= "Loan request approved.";
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }
}
