package com.projecto_0;
import com.projecto_0.config.DatabaseConfig;
import com.projecto_0.controllers.AdminController;
import com.projecto_0.controllers.AuthController;
import com.projecto_0.controllers.LoansController;
import com.projecto_0.controllers.UserController;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class App {
    public static void main(String[] args) throws SQLException {
        //Logback variable
        final Logger logger = LoggerFactory.getLogger(App.class);

        //Conection to DB
        try (Connection connection = DatabaseConfig.getConnection()) {
            logger.info("Succesfull connection from logback");
        } catch (SQLException e) {
            logger.error("Failed to connect to DB: {}", e.getMessage());
        }

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson());
        }).start(7000);

        AdminController adminController = new AdminController();
        UserController userController = new UserController();
        AuthController authController = new AuthController();
        LoansController loansController = new LoansController();

        app.post("/auth/login", authController::login);
        app.post("/logout", AuthController::logout);
        //Endpoint to register a new user
        app.post("/auth/register", authController::registerUser);

        //***************Admins endpoints***************
        // Endpoint to get list of user (only admins)
        app.get("/admin/get-all-users", adminController::getAllUsers);

        app.put("/admin/{id}/approve", adminController::approveLoan);
        app.put("/admin/{id}/reject", adminController::rejectLoan);

        //***************Users and admins endpoints***************
        app.get("/user/get-info",userController::getUserInfo);
        // Endpoint to edit information
        app.patch("/user/update-info", userController::updateInfo);

        app.post("/loan/create", loansController::requestLoan);
        app.get("/loans/{id}", loansController::getLoanById);
        app.put("/loans/edit-loan/{id}", loansController::updateLoan);


    }
}
