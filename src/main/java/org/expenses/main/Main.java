package org.expenses.main;

import org.expenses.model.Expense;
import org.expenses.model.User;
import org.expenses.repository.impl.ExpenseRepositoryImpl;
import org.expenses.repository.impl.UserRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    // this allow to extend the command list in the future and have an easier time managing them
    private enum Command {
        EXPENSE_TRACKER("expense-tracker"),
        ADD("add"),
        LIST("list"),
        SUMMARY("summary"),
        DELETE("delete"),
        ADDUSER("add-user");

        private final String value;

        Command(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Command fromString(String text) {
            for (Command cmd : Command.values()) {
                if (cmd.value.equalsIgnoreCase(text)) {
                    return cmd;
                }
            }
            return null;
        }
    }
    public static void main(String[] args) {

        UserRepositoryImpl userRepository = new UserRepositoryImpl()    ;
        ExpenseRepositoryImpl expenseRepository = new ExpenseRepositoryImpl();

        if(args.length == 0 || !args[0].equalsIgnoreCase(Command.EXPENSE_TRACKER.getValue())){
            System.out.println("Unrecognized command. Please use 'expense-tracker'");
            return;
        }

        String action = args[1];

        // You can use switch with enum values directly
        switch (Command.fromString(action)){
            case ADD -> {
                String description = null;
                String amount = null;
                String userEmail = null;

                // This kind of method are repited with some modification exist any implementation to cover all
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

                //this is the only validation needed? negative amounts?
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
                // catch any other exception
                catch (Exception e) {
                    System.out.println("Error saving expense: " + e.getMessage());
                }
            }

            case DELETE -> {
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
                // Something bad could happen here
                try {
                    expenseRepository.deleteById(id);
                } catch (Exception e) {
                    System.out.println("Error deleting expense: " + e.getMessage());
                }
            }

            case LIST -> {
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
                // Nice implementation 
                expenses.forEach(e -> {
                    System.out.printf("%-5d %-12s %-20s $%s%n",
                            e.getId(), e.getDate(), e.getDescription(), e.getAmount());
                });

            }

            case SUMMARY -> {
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
                    // You can use reduce() to sum the amounts
                    BigDecimal total = expenses.stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    System.out.println("Total expenses: $" + total);

                }else {
                    Integer month = null;
                    try {
                        month = Integer.parseInt(monthString);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid month format");
                        return;
                    }
                    List<Expense> expenses = expenseRepository.listByMonthAndUser(user.getId(), month);
                    // You can use reduce() to sum the amounts in just one operation
                    BigDecimal total = expenses.stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    System.out.println("Total expenses for " +
                            // nice formatting
                            Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                            ": $" + total);
                }

            }
            // you have already the repository for users so it is easy to implement this command
            case ADDUSER -> {
                String name = null;
                String email = null;

                for (int i = 0; i < args.length; i++) {
                    String nameVal = getFlagValue("--name", args, i);
                    if (nameVal != null) name = nameVal;

                    String emailVal = getFlagValue("--email", args, i);
                    if (emailVal != null) email = emailVal;
                }

                if (name == null || email == null) {
                    System.out.println("Name and email are required");
                    return;
                }

                User user = new User(name, email);
                userRepository.save(user);
                System.out.println("User added successfully");
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
