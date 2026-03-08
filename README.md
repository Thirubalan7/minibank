1.Change the Postgre Setting to your database settings
2.Run the SpringbootApplication
3.Create Roles in roles table using query : INSERT INTO roles(role_name) VALUES ('ROLE_MANAGER');
INSERT INTO roles(role_name) VALUES ('ROLE_EMPLOYEE');
INSERT INTO roles(role_name) VALUES ('ROLE_USER');
4.Create Users in users table using query : INSERT INTO users(username,email,password,role_id)
VALUES ('manager','manager@gmail.com','123456',1);

INSERT INTO users(username,email,password,role_id)
VALUES ('employee','employee@gmail.com','123456',2);

INSERT INTO users(username,email,password,role_id)
VALUES ('john','john@gmail.com','123456',3);

5.After Starting the application the Swagger UI will be available at:http://localhost:8084/swagger-ui/index.html

6.In POST/auth/login enter { "email": "manager@gmail.com",
  "password": "123456" } to generate a token, use that token to access API's
  Manager can create employees and users.IN POST /manager/employees to create Employees

7.Employee can create accounts for users. POST/auth/login enter { "email": "employee@gmail.com",
  "password": "123456" } to generate a token,{
  "accountNumber": "ACC1001",
  "balance": 1000,
  "user": {
    "id": 3
  }
}
 




