package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.entity.Transaction;
import com.abraham_bankole.runestone_bank.repository.TransactionRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankStatement {

    private final TransactionRepository transactionRepository;
    private static final String FILE = "";

    // retreive list of transactions within a range of days given an account number
    // generate a pdf of file transaction
    // send the file via email

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate){
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        return transactionRepository.findByAccountNumberAndTimeOfCreationBetween(accountNumber, start, end);
    }

    // generate pdfs for bank statements to send to the user
    public void designStatement(List<Transaction> transactions, String accountNumber) {
        try {
            // Create a unique filename so users don't overwrite each other
            String fileName = "statement_" + accountNumber + "_" + System.currentTimeMillis() + ".pdf";

            // Use the system temp directory
            String filePath = System.getProperty("java.io.tmpdir") + fileName;

            // use 'filePath' in your PDF writer (e.g., iText)
            System.out.println("Generating PDF at: " + filePath);

            // TODO: Add iText PDF generation logic here using 'filePath'
            Rectangle statementSize = new Rectangle(PageSize.A4);
            Document document = new Document(statementSize);
            log.info("setting size of document");

            OutputStream out = new FileOutputStream(filePath);
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable bankStatementTable = new PdfPTable(1);

            PdfPCell bankName = new PdfPCell(new Phrase("RuneStone Bank"));
            bankName.setBorder(Rectangle.NO_BORDER);
            bankName.setBackgroundColor(BaseColor.RED);

            PdfPCell bankAddress = new PdfPCell(new Phrase("317, Runestone Avenue, Lagos, Nigeria"));
            bankAddress.setBorder(Rectangle.NO_BORDER);
            bankAddress.setBackgroundColor(BaseColor.RED);
            bankStatementTable.addCell(bankName);
            bankStatementTable.addCell(bankAddress);

            PdfPTable statementInfo = new PdfPTable(2);
            PdfPCell userInfo = new PdfPCell("Start Date" + );

        } catch (Exception e) {
            e.printStackTrace(); // TODO: Replace with proper logging
        }
    }
}
