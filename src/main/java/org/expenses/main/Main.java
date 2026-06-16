package org.expenses.main;

import org.expenses.model.Expense;
import org.expenses.model.User;
import org.expenses.repository.impl.ExpenseRepositoryImpl;
import org.expenses.repository.impl.UserRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class Main {
    private static final String EXPENSE_TRACKER = "expense-tracker";
    private static final String ACTION_ADD = "add";
    private static final String ACTION_LIST = "list";
    private static final String ACTION_SUMMARY = "summary";
    private static final String ACTION_DELETE = "delete";
    public static void main(String[] args) {

        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        ExpenseRepositoryImpl expenseRepository = new ExpenseRepositoryImpl();

        if(args.length == 0 || !args[0].equalsIgnoreCase(EXPENSE_TRACKER)){
            System.out.println("Unrecognized command. Please use 'expense-tracker'");
            return;
        }

        String action = args[1];

        switch (action){
            case ACTION_ADD -> {
                String description = null;
                String amount = null;
                String userEmail = null;

                for (int i = 0; i < args.length; i++) {
                    String descVal = getFlagValue("--description", args, i);
                    if (descVal != null) description = descVal;

                    String amtVal = getFlagValue("--amount", args, i);
                    if (amtVal != null) amount = amtVal;

                    String emailVal = getFlagValue("--user", args, i);
                    if (emailVal != null) userEmail = emailVal;
                }

                User user = userRepository.findByEmail(userEmail);
                if (user == null){
                    System.out.println("User not found");
                    return;
                }

                if (amount == null){
                    System.out.println("Amount cannot be null");
                    return;
                }

                Integer userId = user.getId();
                try{
                    expenseRepository.save(new Expense(
                            userId,
                            LocalDate.now(),
                            description,
                            new BigDecimal(amount)));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount format");
                }
            }

            case ACTION_DELETE -> {
                String idString = null;
                String userEmail = null;

                for (int i = 0; i < args.length; i++) {
                    String idVal = getFlagValue("--id", args, i);
                    if (idVal != null) idString = idVal;

                    String emailVal = getFlagValue("--user", args, i);
                    if (emailVal != null) userEmail = emailVal;
                }

                if(idString == null){
                    System.out.println("Invalid amount value");
                    return;
                }
                Integer id = null;
                try {
                    id = Integer.parseInt(idString);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid id format");
                    return;
                }

                User user = userRepository.findByEmail(userEmail);
                Expense expense = expenseRepository.getById(id);

                if (user == null){
                    System.out.println("User not found");
                    return;
                }

                if (expense == null){
                    System.out.println("Expense not found");
                    return;
                }

                if (!expense.getUser_id().equals(user.getId())) {
                    System.out.println("You do not have permission to delete this expense");
                    return;
                }

                expenseRepository.deleteById(id);

            }

            case ACTION_LIST -> {
                String userEmail = null;

                for (int i = 0; i < args.length; i++) {
                    String emailVal = getFlagValue("--user", args, i);
                    if (emailVal != null) userEmail = emailVal;
                }

                User user = userRepository.findByEmail(userEmail);

                if(user == null){
                    System.out.println("User not found");
                    return;
                }

                List<Expense> expenses = expenseRepository.listAllByUser(user.getId());
                System.out.printf("%-5s %-12s %-20s %-10s%n", "ID", "Date", "Description", "Amount");
                expenses.forEach(e -> {
                    System.out.printf("%-5d %-12s %-20s $%s%n",
                            e.getId(), e.getDate(), e.getDescription(), e.getAmount());
                });

            }

            case ACTION_SUMMARY -> {
                String userEmail = null;
                String monthString = null;

                for (int i = 0; i < args.length; i++) {
                    String emailVal = getFlagValue("--user", args, i);
                    if (emailVal != null) userEmail = emailVal;

                    String monthVal = getFlagValue("--month", args, i);
                    if (monthVal != null) monthString = monthVal;

                }

                User user = userRepository.findByEmail(userEmail);

                if(user == null){
                    System.out.println("User not found");
                    return;
                }

                if(monthString == null){
                    List<Expense> expenses = expenseRepository.listAllByUser(user.getId());

                    List<BigDecimal> amounts = expenses.stream()
                            .map(Expense::getAmount)
                            .toList();

                    System.out.println("Total expenses: $" + amounts.stream().reduce(BigDecimal.ZERO,BigDecimal::add));

                }else {
                    Integer month = null;
                    try {
                        month = Integer.parseInt(monthString);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid month format");
                        return;
                    }
                    List<Expense> expenses = expenseRepository.listByMonthAndUser(user.getId(), month);

                    List<BigDecimal> amounts = expenses.stream()
                            .map(Expense::getAmount)
                            .toList();

                    System.out.println("Total expenses for " +
                            Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                            ": $" + amounts.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                }

            }

            default -> System.out.println("Invalid Action");
        }


    }

    private static String getFlagValue(String flag, String[] args, int i){
        if(args[i].equals(flag) && i+1< args.length){
            return args[i+1];
        }
        return null;
    }

}
