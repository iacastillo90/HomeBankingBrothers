package com.mindhub.homebanking.controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static com.mindhub.homebanking.models.TransactionType.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createdTransaction(Authentication authentication,
                                                     @RequestParam Double amount,
                                                     @RequestParam String description,
                                                     @RequestParam String numberDestiny,
                                                     @RequestParam String numberOrigin){

        Client client = clientService.findByEmail(authentication.getName());
        Account accountDestiny=accountService.findByNumber(numberDestiny);
        Account accountOrigin=accountService.findByNumber(numberOrigin);
        Set<Account> setNumberOrigin= client.getAccounts();
        String mensaje = " ";

        if ((amount == null) || (description == null) || (numberDestiny == null) || (numberOrigin == null)){
            mensaje = "403 Empty parameters";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }
        if (accountOrigin==null){
            mensaje = "403 The account does not exist";
            return new ResponseEntity<>(mensaje,HttpStatus.FORBIDDEN);
        }
        if (accountDestiny==null){
            mensaje = "Destination account does not exist";
            return new ResponseEntity<>(mensaje,HttpStatus.FORBIDDEN);
        }
        if (numberOrigin.equals(numberDestiny)){
            mensaje = "403 the accounts are the same";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }
        if (setNumberOrigin.contains(numberOrigin)) {
            mensaje = "403 UNAUTHENTICATED ACCOUNT";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }
        if (accountOrigin.getBalance()< amount || amount <= 0){
            mensaje = "You do not have enough balance to make the transaction";
            return new ResponseEntity<>(mensaje,HttpStatus.FORBIDDEN);
        }
        Transaction transactionsOrigin=new Transaction(CREDIT,amount,accountOrigin.getNumber()+description, LocalDateTime.now());
        Transaction transactionsDestiny=new Transaction(DEBIT,amount,accountDestiny.getNumber()+description,LocalDateTime.now());

        accountOrigin.addTransaction(transactionsDestiny);
        accountDestiny.addTransaction(transactionsOrigin);

        Double auxOrigin=accountOrigin.getBalance()-amount;
        Double auxDestiny=accountDestiny.getBalance()+amount;

        accountOrigin.setBalance(auxOrigin);
        accountDestiny.setBalance(auxDestiny);
        accountService.saveAccount(accountDestiny);
        accountService.saveAccount(accountOrigin);

        transactionsOrigin.setBalance(auxDestiny);
        transactionsDestiny.setBalance(auxOrigin);

        transactionService.saveTransaction(transactionsOrigin);
        transactionService.saveTransaction(transactionsDestiny);

        mensaje = "201 Successful transfer";
        return new ResponseEntity<>(mensaje,HttpStatus.CREATED);
    }



    @GetMapping("/transactions/findDate")
    public ResponseEntity<Object> getTransactionsHistory(@RequestParam String dateInit,
                                                            @RequestParam String dateEnd,
                                                            @RequestParam String numberAccount,
                                                            Authentication authentication) throws DocumentException, Exception {
        Client current = clientService.findByEmail(authentication.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (!accountService.existsByNumber(numberAccount)){
            return new ResponseEntity<>("this account dont exist", HttpStatus.BAD_REQUEST);
        }
        if (current == null){
            return new ResponseEntity<>("you are not allowed to see this", HttpStatus.FORBIDDEN);
        }
        if (dateInit == null){
            return new ResponseEntity<>("Please, fill the date requeriment", HttpStatus.BAD_REQUEST);
        }else if (dateEnd == null){
            new ResponseEntity<>("Please, fill the date end requeriment", HttpStatus.BAD_REQUEST);
        }
        if (dateInit.equals(dateEnd)){
            return new ResponseEntity<>("you cannot do this", HttpStatus.BAD_REQUEST);
        }

        String dateInitWithoutTime = dateInit.substring(0, 10); //  "yyyy-MM-dd"
        String dateEndWithoutTime = dateEnd.substring(0, 10); //  "yyyy-MM-dd"

        LocalDate localDateInit = LocalDate.parse(dateInitWithoutTime);
        LocalDate localDateEnd = LocalDate.parse(dateEndWithoutTime);


        List<Transaction> transactionsHistory = transactionService.findByDateBetweenAndAccountNumber(localDateInit.atStartOfDay(), localDateEnd.atTime(23, 59, 59), numberAccount);


        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Image logo = Image.getInstance("C:\\Users\\miguel iligaray\\Desktop\\Ivan\\MindHub\\HomeBankingBrothers\\src\\main\\resources\\static\\web\\assets\\images\\2acd7a1377fa463e974c02cf00d52e75.png");
        logo.scaleToFit(50, 50);
        PdfPTable table = new PdfPTable(4);
        table.addCell("Type");
        table.addCell("Description");
        table.addCell("Amount");
        table.addCell("Date");

        for (Transaction transaction : transactionsHistory) {
            table.addCell(transaction.getType().toString());
            table.addCell(transaction.getDescription());
            table.addCell(String.valueOf(transaction.getAmount()));
            table.addCell(transaction.getDate().format(formatter));
        }
        doc.add(table);
        doc.close();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=transactions.pdf");
        byte[] pdf = out.toByteArray();
        return new ResponseEntity<>(pdf,headers, HttpStatus.OK);
    }
}
