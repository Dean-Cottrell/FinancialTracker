package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FinancialTracker {
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    displayLedger();
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Transaction transaction = parseTransaction(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    private static Transaction parseTransaction(String line) {
        String[] tokens = line.split("\\|");

        if (tokens.length != 5) {
            System.out.println("Error: Invalid transaction format - " + line);
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(tokens[0].trim(), DATE_FORMATTER);
            LocalTime time = LocalTime.parse(tokens[1].trim(), TIME_FORMATTER);
            String description = tokens[2].trim();
            String vendor = tokens[3].trim();
            double amount = Double.parseDouble(tokens[4].trim());

            return new Transaction(date, time, description, vendor, amount);
        } catch (Exception e) {
            System.out.println("Error: Unable to parse transaction - " + line);
            return null;
        }
    }

    private static void addDeposit(Scanner scanner) {
        System.out.println("\nEnter deposit details:");
        System.out.print("Date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
        System.out.print("Time (HH:mm:ss): ");
        LocalTime time = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMATTER);
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();
        System.out.print("Amount: ");

        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount > 0) {
                Transaction deposit = new Transaction(date, time, description, vendor, amount);
                transactions.add(deposit);
                saveTransactionToFile(deposit);
                System.out.println("Deposit added successfully!");
            } else {
                System.out.println("Error: Deposit amount must be positive.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be a valid number.");
        }
    }

    private static void addPayment(Scanner scanner) {
        System.out.println("\nEnter payment details:");
        System.out.print("Date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
        System.out.print("Time (HH:mm:ss): ");
        LocalTime time = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMATTER);
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();
        System.out.print("Amount (negative value for expense): ");
        double amount = Double.parseDouble(scanner.nextLine().trim());

        if (amount < 0) {
            Transaction payment = new Transaction(date, time, description, vendor, amount);
            transactions.add(payment);
            saveTransactionToFile(payment);
            System.out.println("Payment recorded successfully!");
        } else {
            System.out.println("Error: Payment amount must be negative.");
        }
    }

    private static void saveTransactionToFile(Transaction transaction) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(transaction.date() + "|" + transaction.time() + "|" +
                    transaction.description() + "|" + transaction.vendor() + "|" + transaction.amount());
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    private static void displayLedger() {
        System.out.println("\n--- Ledger ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction.date() + " | " + transaction.time() + " | " +
                    transaction.description() + " | " + transaction.vendor() + " | $" +
                    String.format("%.2f", transaction.amount()));
        }

        System.out.println("----------------");
    }

    private static void displayDeposits() {
        System.out.println("\n---- Deposits ----");

        for (Transaction transaction : transactions) {
            if (transaction.amount() > 0) {
                System.out.println(transaction);
            }
        }

        System.out.println("------------------");
    }

    private static void displayPayments() {
        System.out.println("\n---- Payments ----");

        for (Transaction transaction : transactions) {
            if (transaction.amount() < 0) {
                System.out.println(transaction);
            }
        }

        System.out.println("------------------");
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();
            if (input.equals("0")) running = false;
            else {
                generateReports(input, scanner);
            }
        }
    }

    private static void generateReports(String option, Scanner scanner) {
        switch (option) {
            case "1" -> generateMonthToDateReport();
            case "2" -> generatePreviousMonthReport();
            case "3" -> generateYearToDateReport();
            case "4" -> generatePreviousYearReport();
            case "5" -> {
                System.out.print("Enter vendor name: ");
                scanner.nextLine();
                filterTransactionsByVendor();
            }
            default -> System.out.println("Invalid option.");
        }
}

    private static void filterTransactionsByVendor() {
    }

    private static void generateMonthToDateReport() {
}

    private static void generatePreviousMonthReport() {
}

    private static void generateYearToDateReport() {
}

    private static void generatePreviousYearReport() {
}

    public record Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {}
}



