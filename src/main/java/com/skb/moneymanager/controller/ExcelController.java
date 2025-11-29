package com.skb.moneymanager.controller;

import com.skb.moneymanager.service.ExcelService;
import com.skb.moneymanager.service.ExpenseService;
import com.skb.moneymanager.service.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1.0/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    // Download Income Excel
    @GetMapping("download/income")
    public void downloadIncomeExcel(HttpServletResponse response) throws IOException {
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        String filename = "Income_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        var incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        excelService.writeIncomesToExcel(response.getOutputStream(), incomes);
    }

    // Download Expense Excel
    @GetMapping("download/expense")
    public void downloadExpenseExcel(HttpServletResponse response) throws IOException {
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        String filename = "Expense_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        var expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        excelService.writeExpensesToExcel(response.getOutputStream(), expenses);
    }
}
