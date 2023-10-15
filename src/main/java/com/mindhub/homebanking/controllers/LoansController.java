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

        // Establecer los porcentajes de interés basados en el tipo de préstamo
        switch (loanApplicationDTO.getTypeName()) {
            case "MORTGAGE":
                loanApplicationDTO.setInterest(0.20);
                break;
            case "PERSONAL":
                loanApplicationDTO.setInterest(0.15);
                break;
            case "AUTOMOTIVE":
                loanApplicationDTO.setInterest(0.25);
                break;
            default:

                mensaje = "Invalid loan type.";
                return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Verificar que el préstamo existe
        Loan loan = loanService.findByName(LoanType.valueOf(loanApplicationDTO.getTypeName()));
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

        if (existingLoans >= 1) {
            mensaje = "You have reached the maximum number of loans of this type.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        // Calcular el interés del préstamo
        Double loanInterest = loanApplicationDTO.getAmount() * (1 + loanApplicationDTO.getInterest());
        loan.setInterest(loanInterest);

        // Crear solicitud de préstamo
        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount(), loanApplicationDTO.getPayments().get(0), client, loan);
        clientLoanService.saveClientLoan(clientLoan);

        // Crear transacción
        Transaction transactionLoanCredit = new Transaction(
                TransactionType.CREDIT,
                loanApplicationDTO.getAmount(),
                loan.getName() + " loan approved",
                LocalDateTime.now()
        );

        // Actualizar el balance de la cuenta de destino
        double newBalance = destinationAccount.getBalance() + loanApplicationDTO.getAmount();
        destinationAccount.setBalance(newBalance);
        destinationAccount.addTransaction(transactionLoanCredit);
        accountService.saveAccount(destinationAccount);

        transactionLoanCredit.setBalance(newBalance);
        transactionService.saveTransaction(transactionLoanCredit);

        mensaje= "Loan request approved.";
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }

        @PostMapping("/admin/loan")
        public ResponseEntity<?> createLoan(Authentication authentication, @RequestBody LoanDTO loanDTO) {
            try {
                Client client = clientService.findByEmail(authentication.getName());
                if (!client.getEmail().contains("@admin.com")) {
                    return new ResponseEntity<>("Not authorized.", HttpStatus.FORBIDDEN);
                }
                if (loanDTO.getInterest() == 0 || loanDTO.getMaxAccount() == 0 || loanDTO.getName() == null || loanDTO.getPayments().isEmpty()) {
                    return new ResponseEntity<>("Empty spaces.", HttpStatus.FORBIDDEN);
                }

                loanService.saveLoan(new Loan(loanDTO.getName(), loanDTO.getMaxAccount(), loanDTO.getPayments(), loanDTO.getInterest()));
                return new ResponseEntity<>("New loan attached.", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("Request error.", HttpStatus.BAD_REQUEST);
            }
        }
};
