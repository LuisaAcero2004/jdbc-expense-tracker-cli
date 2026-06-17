package org.expenses.repository;

import org.expenses.model.Expense;

import java.util.List;
// Great interface
public interface ExpenseRepository extends Repository<Expense>{

    List<Expense> listByMonthAndUser(Integer userId, Integer month);
    List<Expense> listAllByUser(Integer userId);

}
