package com.skb.moneymanager.controller;

import com.skb.moneymanager.entity.ProfileEntity;
import com.skb.moneymanager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("api/v1.0/email")
@RequiredArgsConstructor
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;

    // Send Income Excel via Email
    @GetMapping("/income-excel")
    public ResponseEntity<String> emailIncomeExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomeService.getCurrentMonthIncomesForCurrentUser());

        String subject = "Your Income Excel Report";
        String htmlBody = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #1F2937; line-height: 1.6;">
                  <h2 style="color: #4F46E5;">Hello %s,</h2>
                  <p>We have prepared your <strong>income report for this month</strong>. Please find it attached in Excel format.</p>
                  <p style="margin-top: 20px;">Thank you for using <strong>Money Manager</strong> to track your finances!</p>
                  <p style="margin-top: 40px; font-size: 0.9em; color: #6B7280;">This is an automated email, please do not reply.</p>
                </body>
                </html>
                """.formatted(profile.getFullName());

        emailService.sendEmailWithAttachment(
                profile.getEmail(),
                subject,
                htmlBody,
                baos.toByteArray(),
                "Income_Report.xlsx"
        );

        return ResponseEntity.ok("Income Excel report sent to " + profile.getEmail());
    }

    // Send Expense Excel via Email
    @GetMapping("/expense-excel")
    public ResponseEntity<String> emailExpenseExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenseService.getCurrentMonthExpensesForCurrentUser());

        String subject = "Your Expense Excel Report";
        String htmlBody = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #1F2937; line-height: 1.6;">
                  <h2 style="color: #EF4444;">Hello %s,</h2>
                  <p>We have prepared your <strong>expense report for this month</strong>. Please find it attached in Excel format.</p>
                  <p style="margin-top: 20px;">Thank you for using <strong>Money Manager</strong> to track your finances!</p>
                  <p style="margin-top: 40px; font-size: 0.9em; color: #6B7280;">This is an automated email, please do not reply.</p>
                </body>
                </html>
                """.formatted(profile.getFullName());

        emailService.sendEmailWithAttachment(
                profile.getEmail(),
                subject,
                htmlBody,
                baos.toByteArray(),
                "Expense_Report.xlsx"
        );

        return ResponseEntity.ok("Expense Excel report sent to " + profile.getEmail());
    }
}
