package com.projecto_0.controllers;
import com.projecto_0.dao.UserDao;
import com.projecto_0.models.UserModel;
import com.projecto_0.service.AuthService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {
    private final UserDao userDao = new UserDao();
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public void login(Context ctx) {
        logger.info("Login request received.");
        // Get the request body data (JSON)
        UserModel requestUser = ctx.bodyAsClass(UserModel.class);

        // Verify that email and password exist
        if (requestUser.getEmail() == null || requestUser.getPassword() == null) {
            logger.warn("Login failed: Missing email or password.");
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Missing email or password\"}");
            return;
        }

        // Search for a user in the database by email
        UserModel user = userDao.getUserByEmail(requestUser.getEmail());

        // If the user does not exist
        if (user == null) {
            logger.warn("Login failed: User not found for email {}.", requestUser.getEmail());
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"Invalid credentials\"}");
            return;
        }

        // Verify that the entered password matches the stored (hashed) password
        AuthService authService = new AuthService();
        if (!authService.checkPassword(requestUser.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Invalid password for user {}.", requestUser.getEmail());
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"Invalid credentials\"}");
            return;
        }

        // Login and store the user in the session
        HttpSession session = ctx.req().getSession(true);
        session.setAttribute("user", user);

        logger.info("Login successful for user {}.", requestUser.getEmail());
        ctx.status(HttpStatus.OK).json("{\"message\":\"Login successful\"}");
    }

    public static void logout(Context ctx) {
        logger.info("Logout request received.");

        HttpSession session = ctx.req().getSession();
        if (session != null) {
            session.invalidate();
            logger.info("Logged out.");
        }else{
            logger.warn("No session found to invalidate.");
        }
        ctx.status(200).json("{\"message\":\"Logged out\"}");
    }

    public void registerUser(Context ctx) {
        logger.info("Register user request received.");
        UserModel newUser = ctx.bodyAsClass(UserModel.class);

        if (newUser.getName() == null || newUser.getLastName() == null ||
                newUser.getEmail() == null || newUser.getPassword() == null ||
                (newUser.getRoleId() != 1 && newUser.getRoleId() != 2)) {
            logger.warn("Registration failed: Missing or invalid fields.");
            ctx.status(HttpStatus.BAD_REQUEST).result("All fields are required: name, lastName, email, password, roleId (1 or 2).");
            return;
        }

        if (userDao.emailExists(newUser.getEmail())) {
            logger.warn("Registration failed: Email {} is already registered.", newUser.getEmail());
            ctx.status(HttpStatus.CONFLICT).result("Email is already registered.");
            return;
        }

        boolean success = userDao.registerUser(newUser);

        if (success) {
            logger.info("User {} registered successfully.", newUser.getEmail());
            ctx.status(HttpStatus.CREATED).json("{\"message\":\"User registered successfully.\"}");
        } else {
            logger.error("Registration failed: Error registering user {}.", newUser.getEmail());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Error registering user.\"}");
        }
    }

}