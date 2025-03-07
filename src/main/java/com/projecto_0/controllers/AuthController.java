package com.projecto_0.controllers;
import com.projecto_0.dao.UserDao;
import com.projecto_0.models.UserModel;
import com.projecto_0.service.AuthService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

public class AuthController {
    private final UserDao userDao = new UserDao();

    public void login(Context ctx) {
        // Get the request body data (JSON)
        UserModel requestUser = ctx.bodyAsClass(UserModel.class);

        // Verify that email and password exist
        if (requestUser.getEmail() == null || requestUser.getPassword() == null) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Missing email or password\"}");
            return;
        }

        // Search for a user in the database by email
        UserModel user = userDao.getUserByEmail(requestUser.getEmail());

        // If the user does not exist
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"Invalid credentials\"}");
            return;
        }

        // Verify that the entered password matches the stored (hashed) password
        AuthService authService = new AuthService();
        if (!authService.checkPassword(requestUser.getPassword(), user.getPassword())) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"Invalid credentials\"}");
            return;
        }

        // Login and store the user in the session
        HttpSession session = ctx.req().getSession(true);
        session.setAttribute("user", user);

        ctx.status(HttpStatus.OK).json("{\"message\":\"Login successful\"}");
    }

    public static void logout(Context ctx) {
        HttpSession session = ctx.req().getSession();
        if (session != null) {
            session.invalidate();
        }
        ctx.status(200).json("{\"message\":\"Logged out\"}");
    }

    public void registerUser(Context ctx) {
        UserModel newUser = ctx.bodyAsClass(UserModel.class);

        if (newUser.getName() == null || newUser.getLastName() == null ||
                newUser.getEmail() == null || newUser.getPassword() == null ||
                (newUser.getRoleId() != 1 && newUser.getRoleId() != 2)) {
            ctx.status(HttpStatus.BAD_REQUEST).result("All fields are required: name, lastName, email, password, roleId (1 or 2).");
            return;
        }

        if (userDao.emailExists(newUser.getEmail())) {
            ctx.status(HttpStatus.CONFLICT).result("Email is already registered.");
            return;
        }

        boolean success = userDao.registerUser(newUser);

        if (success) {
            ctx.status(HttpStatus.CREATED).json("{\"message\":\"User registered successfully.\"}");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Error registering user.\"}");
        }
    }

}