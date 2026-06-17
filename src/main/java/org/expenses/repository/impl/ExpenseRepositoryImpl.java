package org.expenses.repository.impl;

import org.expenses.model.Expense;
import org.expenses.repository.ExpenseRepository;
import org.expenses.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepositoryImpl implements ExpenseRepository {

    public void save(Expense expense){
        String sql;
        if(expense.getId() == null){
            sql = "INSERT INTO expenses (user_id, date, description, amount) VALUES (?, ?, ?, ?)";
        }else{
            // Should not this been other method? for update?
            sql = "UPDATE expenses SET date = ?, description = ?, amount = ? WHERE id = ?";
        }

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if(expense.getId() == null){
                preparedStatement.setInt(1,expense.getUser_id());
                preparedStatement.setDate(2, Date.valueOf(expense.getDate()));
                preparedStatement.setString(3, expense.getDescription());
                preparedStatement.setBigDecimal(4,expense.getAmount());
            }else{
                preparedStatement.setDate(1, Date.valueOf(expense.getDate()));
                preparedStatement.setString(2, expense.getDescription());
                preparedStatement.setBigDecimal(3, expense.getAmount());
                preparedStatement.setInt(4, expense.getId());
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Expense getById(Integer id){
        String sql = "SELECT * FROM expenses WHERE id = ?";
        Expense expense = null;
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    expense = createExpense(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return expense;

    }

    public void deleteById(Integer id){
        String sql = "DELETE FROM expenses WHERE id = ?";
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Expense> listAll(){
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses";
        try(Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()){
                expenses.add(createExpense(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  expenses;

    }

    public List<Expense> listByMonthAndUser(Integer userId, Integer month){
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? AND MONTH(date) = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2, month);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()){
                    expenses.add(createExpense(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return expenses;

    }

    @Override
    public List<Expense> listAllByUser(Integer userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()){
                    expenses.add(createExpense(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return expenses;

    }

    public Expense createExpense(ResultSet resultSet) throws SQLException {
        return new Expense(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getDate("date") == null ? null : resultSet.getDate("date").toLocalDate(),
                resultSet.getString("description"),
                resultSet.getBigDecimal("amount")
        );

    }

}
