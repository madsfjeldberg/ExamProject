# AlphaPlan
### The last project tool you'll ever use.


## How it Works

AlphaPlan is a web-based program, designed to help teams work efficiently together.

Features:
- Create an account. 
- Log in and log out.
- Safety features to prevent unauthorized access.
- Create, edit and delete projects.
- Create, edit and delete tasks.
- Assign tasks to users.
- See how many hours each task/projekt takes to finish.

The program allows users to create, edit and delete projects and tasks, as well as assign tasks to users.

Information about users, projects and tasks, are stored locally or in the cloud through the use of 
a MySQL database.

## Technologies Used


This project is built using the following technologies:

- **Java**: The main programming language used for developing the project.
- **Spring Boot**: A framework used for creating stand-alone, production-grade Spring based applications.
- **thymeleaf**: A modern server-side Java template engine for web and standalone environments.
- **MySQL**: Used for managing and manipulating the database.
- **Maven**: A build automation tool used primarily for Java projects.

## Setup and Installation

This section provides instructions on how to set up and install the project for development or usage.

AlphaPlan has been built using IntelliJ 2024.1.1 and Java 17.

To run the project, you will need to have the following installed:

- Java 17
- Maven
- MySQL
- IntelliJ IDEA

Steps to run the project:

1. Create a Local (or cloud) MySQL Database. We recommend using MySQL Workbench to manage the database.
2. Copy the script from `SQL/create_database.sql` and run it in your MySQL Workbench to create the database. 
3. Clone the repository to your local machine. 
4. Open the project in IntelliJ IDEA. 
5. Set your active profile to `local` in `application.properties`.
6. edit the application-local.properties file to match your database settings.
7. Run the project. 
8. Open your browser and go to `http://localhost:8080` to view the project.

If you want to host the project in the cloud, you can use Azure:

1. Create a MySQL database in Azure.
2. Create a new repository in GitHub and push the project to the repository.
3. Create a Web App in Azure, and link it to your repository.
4. Go to www.github.com/YourUsername/YourRepository/settings/secrets/actions and add the following secrets:
   - `JDBC_DATABASE_URL`: The URL of your MySQL database.
   - `JDBC_DATABASE_USERNAME`: The username of your MySQL database.
   - `JDBC_DATABASE_PASSWORD`: The password of your MySQL database.
5. Go to your Web App in Azure and click on "Environment variables", and add the above secrets there as well.
6. After pushing the project to the repository, the project will be automatically deployed to Azure.

## Contributing

We'd love to have you help us improve AlphaPlan. To contribute, please see [CONTRIBUTE.md](CONTRIBUTE.md).
