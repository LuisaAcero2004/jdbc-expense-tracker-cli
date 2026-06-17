# My journey to becoming a Java Developer ☕
## Project 1 - Expense Tracker using JDBC
This is a CLI application created to track expenses using JDBC and MySQL.

Course: [Platzi JDBC](https://platzi.com/cursos/java-sql/)

Challenge: [Roadmsp.sh Expense Tracker Project](https://roadmap.sh/projects/expense-tracker)


### Tech Stack
* Language: Java 17
* Database: MySQL
* Driver: JDBC (Java Database Connectivity)
* Build Tool: Maven

### Installation Guide
1. Clone the repository
  ```
  git clone https://github.com/LuisaAcero2004/jdbc-expense-tracker-cli.git
  ```

2. Create the database

   You can use the following SQL to create the database
   ```
     -- 1. Create database
    CREATE DATABASE expenseTracker;
    USE expenseTracker;
  
    -- 2. Create users table
    CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        email VARCHAR(150) NOT NULL UNIQUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    
    -- 3. Create expenses table
    CREATE TABLE expenses (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        date DATE NOT NULL,
        description VARCHAR(255),
        amount DECIMAL(10, 2) NOT NULL,
        FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
    );
   ```
4. Add the database.properties file

   Create a new properties file in the path `src/main/resources/database.properties` use the following format:
   ```
    url=<YOUR_URL>
    user=<YOUR_USER>
    password=<YOUR_PASSWORD>
   ```
### How to run
```
# Add expense
mvn exec:java -Dexec.args="expense-tracker add --user 'test@test.com' --description 'Lunch' --amount 40.50"

# List expenses
mvn exec:java -Dexec.args="expense-tracker list --user 'test@test.com'"
  
# Get summary of expenses
mvn exec:java -Dexec.args="expense-tracker summary --month 6 --user 'test@test.com'"
mvn exec:java -Dexec.args="expense-tracker summary --user 'test@test.com'"
  
# Delete expense
mvn exec:java -Dexec.args="expense-tracker delete --user 'test@test.com' --id 4"
```
### Key takeaways
* Use of Singleton for the database connection to prevent opening multiple redundant connections and optimize resource usage
* Use of `try-with-resources` to close the database connections
* Use of `Statement` and `PreparedStatement` to create SQL statements for the CRUD operations
* Implementation of Repository design pattern

    

