package org.expenses.repository.impl;

import org.expenses.model.User;
import org.expenses.repository.UserRepository;
import org.expenses.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    public void save(User user){
        String sql;
        if(user.getId()==null){
            sql = "INSERT INTO users (name, email) VALUES (?,?)";
        }else{
            sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        }

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            if(user.getId()==null){
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
            }else{
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setInt(3, user.getId());
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public User getById(Integer id){
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    user = createUser(resultSet);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return user;
    }

    public void deleteById(Integer id){
        String sql = "DELETE FROM users WHERE id = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public List<User> listAll(){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try(Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            while (resultSet.next()){
                users.add(createUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;

    }

    public User findByEmail(String email){
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, email);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    user = createUser(resultSet);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return user;
    }

    // Why this is not used ? 
    private User createUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getTimestamp("created_at") == null ? null:
                resultSet.getTimestamp("created_at").toLocalDateTime());
    }


}
