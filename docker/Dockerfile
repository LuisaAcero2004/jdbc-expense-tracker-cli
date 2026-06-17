FROM mysql:8.0

# Environment variables for the test database
ENV MYSQL_ROOT_PASSWORD=rootpassword
ENV MYSQL_DATABASE=expenseTracker
ENV MYSQL_USER=expenseuser
ENV MYSQL_PASSWORD=expensepass

# Copy the schema init script so MySQL runs it on first startup
COPY init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 3306
