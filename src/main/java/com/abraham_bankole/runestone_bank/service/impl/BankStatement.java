package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.entity.Transaction;
import com.abraham_bankole.runestone_bank.entity.User;
import com.abraham_bankole.runestone_bank.repository.TransactionRepository;
import com.abraham_bankole.runestone_bank.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankStatement {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.application.name}")
    private String bankName; // Injected from application.properties

    private static final String BANK_ADDRESS = "317, Runestone Avenue, Lagos, Nigeria";
    private static final String EMAIL_FROM = "no-reply@runestonebank.com";
    private static final String EMAIL_SUBJECT = "RuneStone Bank Statement";
    private static final String EMAIL_TEXT = "Dear Customer,\n\nPlease find attached your requested account statement.\n\nRegards,\nRuneStone Bank";

    // retreive list of transactions within a range of days given an account number
    // generate a pdf of file transaction
    // send the file via email

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, FileNotFoundException, MessagingException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        // generate pdfs for bank statements to send to the user
        // Create a unique filename so users don't overwrite each other
        String fileName = "statement_" + accountNumber + "_" + System.currentTimeMillis() + ".pdf";

        // Use the system temp directory
        String filePath = System.getProperty("java.io.tmpdir") + "/" + fileName;

        // use 'filePath' in your PDF writer (e.g., iText)
        System.out.println("Generating PDF at: " + filePath);

        User user = userRepository.findByAccountNumber(accountNumber);
        String userName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        // TODO: Add iText PDF generation logic here using 'filePath'
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");

        OutputStream out = new FileOutputStream(filePath);
        PdfWriter.getInstance(document, out);
        document.open();

        PdfPTable bankStatementTable = new PdfPTable(1);

        PdfPCell bankNameCell = new PdfPCell(new Phrase(bankName)); // Uses injected value
        bankNameCell.setBorder(Rectangle.NO_BORDER);
        bankNameCell.setBackgroundColor(BaseColor.RED);

        PdfPCell bankAddressCell = new PdfPCell(new Phrase(BANK_ADDRESS));
        bankAddressCell.setBorder(Rectangle.NO_BORDER);
        bankAddressCell.setBackgroundColor(BaseColor.RED);
        bankStatementTable.addCell(bankNameCell);
        bankStatementTable.addCell(bankAddressCell);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell userInfo = new PdfPCell(new Phrase("Start Date: " + startDate));
        userInfo.setBorder(Rectangle.NO_BORDER);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCCOUNT"));
        statement.setBorder(Rectangle.NO_BORDER);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate));
        stopDate.setBorder(Rectangle.NO_BORDER);

        PdfPCell customerName = new PdfPCell(new Phrase("Customer Name: " + userName));
        customerName.setBorder(Rectangle.NO_BORDER);
        //padding
        PdfPCell padding = new PdfPCell();
        padding.setBorder(Rectangle.NO_BORDER);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        address.setBorder(Rectangle.NO_BORDER);

        statementInfo.addCell(userInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(customerName);
        statementInfo.addCell(padding);
        statementInfo.addCell(address);

        // the real table
        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.RED);
        date.setBorder(Rectangle.NO_BORDER);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBorder(Rectangle.NO_BORDER);
        transactionType.setBackgroundColor(BaseColor.RED);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBorder(Rectangle.NO_BORDER);
        transactionAmount.setBackgroundColor(BaseColor.RED);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.RED);
        status.setBorder(Rectangle.NO_BORDER);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        // dynamically populate the table
        List<Transaction> transactionList = transactionRepository.findByAccountNumberAndTimeOfCreationBetween(accountNumber, start, end);

        for (Transaction transaction : transactionList) {
            transactionTable.addCell(new Phrase(transaction.getTimeOfCreation().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(String.valueOf(transaction.getAmount())));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        }

        document.add(bankStatementTable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

        sendEmailWithAttachment(user.getEmail(), filePath, fileName);

        return transactionList;
    }

    private void sendEmailWithAttachment(String toEmail, String filePath, String fileName) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(EMAIL_FROM);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(EMAIL_SUBJECT);
        mimeMessageHelper.setText(EMAIL_TEXT);

        FileSystemResource file = new FileSystemResource(new File(filePath));
        mimeMessageHelper.addAttachment(fileName, file);

        javaMailSender.send(mimeMessage);
        log.info("Email sent successfully to {}", toEmail);
    }
}