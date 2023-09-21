package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class HomebankingApplication {
    @Autowired
    public PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
        return (args) -> {
            Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("123456"));
            Client client2 = new Client("Tony", "Stark", "stark@mindhub.com", passwordEncoder.encode("123456"));
            Client admin = new Client("admin", "admin", "admin@mindhub.com", passwordEncoder.encode("1234"));

            clientRepository.save(client1);
            clientRepository.save(client2);
            clientRepository.save(admin);

            passwordEncoder.encode(client1.getPassword());
            passwordEncoder.encode(client2.getPassword());
            passwordEncoder.encode(admin.getPassword());

            Account account1 = new Account("VIN001", LocalDate.now(), 5000.00, TypeAccounts.SAVINGS);
            Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500.00, TypeAccounts.CURRENT);

            client1.addAccount(account1);
            client1.addAccount(account2);

            accountRepository.save(account1);
            accountRepository.save(account2);

            Loan mortgage = new Loan(LoanType.MORTGAGE, 500000.0, Arrays.asList(12, 24, 36, 48, 60),20.50);
            Loan personal = new Loan(LoanType.PERSONAL, 100000.0, Arrays.asList(6, 12, 24),20.50);
            Loan automotive= new Loan(LoanType.AUTOMOTIVE, 300000.0, Arrays.asList(6, 12, 24, 36),20.50);

            loanRepository.save(mortgage);
            loanRepository.save(personal);
            loanRepository.save(automotive);

            ClientLoan clientLoan1 = new ClientLoan(400000.0, 60, client1, mortgage);
            ClientLoan clientLoan2 = new ClientLoan(50000.0, 12, client1, personal);

            ClientLoan clientLoan3 = new ClientLoan(100000.0, 24, client2, personal);
            ClientLoan clientLoan4 = new ClientLoan(200000.0, 36, client2, automotive);

            client1.addClientLoan(clientLoan1);
            client1.addClientLoan(clientLoan2);
            client2.addClientLoan(clientLoan3);
            client2.addClientLoan(clientLoan4);

            clientRepository.save(client1);
            clientRepository.save(client2);

            clientLoanRepository.save(clientLoan1);
            clientLoanRepository.save(clientLoan2);
            clientLoanRepository.save(clientLoan3);
            clientLoanRepository.save(clientLoan4);

            Transaction account1Tran1 = new Transaction(TransactionType.CREDIT, 5300.00, "restaurant lunch", LocalDateTime.now());
            Transaction account1Tran4 = new Transaction(TransactionType.DEBIT, -3900.00, "grocery shopping", LocalDateTime.now().plusDays(3));
            Transaction account1Tran5 = new Transaction(TransactionType.DEBIT, -1400.00, "gas station", LocalDateTime.now().plusDays(2));


            account1Tran1.setBalance(account1.getBalance()+account1Tran1.getAmount());
            account1.setBalance(account1Tran1.getBalance());
            account1Tran4.setBalance(account1.getBalance()+account1Tran4.getAmount());
            account1.setBalance(account1Tran4.getBalance());
            account1Tran5.setBalance(account1.getBalance()+account1Tran5.getAmount());
            account1.setBalance(account1Tran5.getBalance());

            account1.addTransaction(account1Tran1);
            account1.addTransaction(account1Tran4);
            account1.addTransaction(account1Tran5);

            Transaction account2Tran2 = new Transaction(TransactionType.CREDIT, 6000.00, "Free Lance Job", LocalDateTime.now().plusDays(1));
            Transaction account2Tran3 = new Transaction(TransactionType.DEBIT, -400.00, "coffee shop", LocalDateTime.now().plusDays(2));
            Transaction account2Tran6 = new Transaction(TransactionType.DEBIT, -5600.00, "electronics store", LocalDateTime.now().plusDays(3));

            account2Tran2.setBalance(account2.getBalance()+account2Tran2.getAmount());
            account2.setBalance(account2Tran2.getBalance());
            account2Tran3.setBalance(account2.getBalance()+account2Tran3.getAmount());
            account2.setBalance(account2Tran3.getBalance());
            account2Tran6.setBalance(account2.getBalance()+account2Tran6.getAmount());
            account2.setBalance(account2Tran6.getBalance());

            account2.addTransaction(account2Tran2);
            account2.addTransaction(account2Tran3);
            account2.addTransaction(account2Tran6);


            transactionRepository.saveAll(Arrays.asList(account1Tran1, account1Tran4, account1Tran5, account2Tran2, account2Tran3, account2Tran6));

            LocalDate currentDate = LocalDate.now();
            LocalDate expirationDate = currentDate.plusYears(5);

            //Prueba tarjeta vencida
            LocalDate expiredExampleDate = LocalDate.now().minusYears(5);
            LocalDate expirationExampleDate = expiredExampleDate.plusYears(5);

            Card card1 = new Card(client1.getFirstName() + " " + client1.getLastName(),
                    CardType.DEBIT, CardColor.GOLD, "1234-5678-9012-3456", 123, expirationExampleDate, expiredExampleDate, client1, false);

            Card card2 = new Card(client1.getFirstName() + " " + client1.getLastName(),
                    CardType.CREDIT, CardColor.TITANIUM, "9876-5432-1098-7654", 456, expirationDate, currentDate, client1, true);

            Card card3 = new Card(client2.getFirstName() + " " + client2.getLastName(),
                    CardType.CREDIT, CardColor.SILVER, "5678-9012-3456-7890", 789, expirationDate, currentDate, client2, true);


            client1.addCard(card1);
            client1.addCard(card2);
            client2.addCard(card3);


            cardRepository.save(card1);
            cardRepository.save(card2);
            cardRepository.save(card3);



        };
    }

}
