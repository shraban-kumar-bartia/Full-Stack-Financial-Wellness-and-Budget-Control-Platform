package com.skb.moneymanager.service;

import com.skb.moneymanager.dto.ExpenseDTO;
import com.skb.moneymanager.entity.ProfileEntity;
import com.skb.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    // @Scheduled(cron = "0 * * * * *", zone = "IST")
    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<body style='font-family:Arial,sans-serif; background-color:#f5f7fb; padding:20px;'>" +
                            "<div style='max-width:600px; margin:auto; background:#ffffff; border-radius:10px; " +
                            "box-shadow:0 2px 8px rgba(0,0,0,0.05); padding:24px;'>" +

                            "<h2 style='color:#333333; margin-top:0;'>Daily Reminder</h2>" +

                            "<p style='font-size:15px; color:#444;'>Hi " + profile.getFullName() + ",</p>" +

                            "<p style='font-size:15px; color:#444; line-height:1.6;'>"
                            + "This is a friendly reminder to update your <b>income and expenses</b> "
                            + "in <span style='color:#1a7f37; font-weight:bold;'>Money Manager</span> today."
                            + "</p>" +

                            "<p style='text-align:center; margin:30px 0;'>"
                            + "<a href='" + frontendUrl + "' "
                            + "style='display:inline-block; padding:12px 20px; background-color:#1a7f37; "
                            + "color:#ffffff; text-decoration:none; border-radius:6px; "
                            + "font-weight:bold; font-size:15px;'>"
                            + "Go to Money Manager</a>"
                            + "</p>" +

                            "<p style='font-size:13px; color:#666;'>"
                            + "If the button doesn’t work, copy and paste this link in your browser:<br>"
                            + "<a href='" + frontendUrl + "' style='color:#175cd3; text-decoration:none;'>"
                            + frontendUrl + "</a>"
                            + "</p>" +

                            "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'>" +

                            "<p style='font-size:13px; color:#777;'>Best regards,<br><b>Money Manager Team</b></p>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            emailService.sendEmail(
                    profile.getEmail(),
                    "Daily reminder: Add your income and expense",
                    body
            );
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }

//     @Scheduled(cron = "0 * * * * *", zone = "IST")
    @Scheduled(cron = "0 0 * * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses = expenseService.getExpenseForUserOnDate(profile.getId(), LocalDate.now());

            if (!todaysExpenses.isEmpty()) {
                double totalAmount = todaysExpenses.stream()
                        .mapToDouble(expense -> expense.getAmount().doubleValue())
                        .sum();

                StringBuilder table = new StringBuilder();

                // Expense table with all expenses for today
                table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;font-size:14px;'>")
                        .append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
                        .append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : todaysExpenses) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid #ddd;padding:8px;text-align:center;'>").append(i++).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>₹").append(expense.getAmount()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A")
                            .append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(LocalDate.now()).append("</td>")
                            .append("</tr>");
                }

                // Add total row
                table.append("<tr style='background-color:#f9fafb;font-weight:bold;'>")
                        .append("<td colspan='2' style='border:1px solid #ddd;padding:8px;text-align:right;'>Total:</td>")
                        .append("<td style='border:1px solid #ddd;padding:8px;'>₹").append(String.format("%.2f", totalAmount)).append("</td>")
                        .append("<td colspan='2' style='border:1px solid #ddd;padding:8px;'></td>")
                        .append("</tr>")
                        .append("</table>");

                String body =
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<body style='font-family:Arial,sans-serif; background-color:#f5f7fb; padding:20px;'>" +
                                "<div style='max-width:600px; margin:auto; background:#ffffff; border-radius:10px; " +
                                "box-shadow:0 2px 8px rgba(0,0,0,0.05); padding:24px;'>" +

                                "<p style='font-size:15px; color:#444;'>Hi " + profile.getFullName() + ",</p>" +

                                "<p style='font-size:15px; color:#444; line-height:1.6;'>Here is a summary of your expenses for today:</p>" +

                                table.toString() +

                                "<br/><p style='font-size:13px; color:#777;'>Best regards,<br><b>Money Manager Team</b></p>" +
                                "</div>" +
                                "</body>" +
                                "</html>";

                emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary", body);
            }
        }
        log.info("Job completed: sendDailyExpenseSummary()");
    }
}
