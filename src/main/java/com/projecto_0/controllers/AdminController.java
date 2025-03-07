package com.projecto_0.controllers;
import com.projecto_0.dao.LoanDao;
import com.projecto_0.dao.UserDao;
import com.projecto_0.models.UserModel;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class AdminController {
    private final UserDao userDao = new UserDao();
    private final LoanDao loanDao = new LoanDao();

    public void getAllUsers(Context ctx) {
        // Get current session
        HttpSession session = ctx.req().getSession(false); // false to not create a new session if it doesn't exist

        // Check if the user is logged in
        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to access this resource.\"}");
            return;
        }

        // Get the user from session
        UserModel user = (UserModel) session.getAttribute("user");

        // Check if the user has the correct role (role_id = 1)
        if (user.getRoleId() != 1) {
            ctx.status(HttpStatus.FORBIDDEN).json("{\"error\":\"Unauthorized user.\"}");
            return;
        }

        // Get the list of all users
        List<UserModel> users = userDao.getAllUsers();

        ctx.json(users);
    }

    public void approveLoan(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to access this resource.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");

        if (user.getRoleId() != 1) {
            ctx.status(HttpStatus.FORBIDDEN).json("{\"error\":\"Unauthorized user.\"}");
            return;
        }

        // Get the loan ID from the route parameters
        String loanIdParam = ctx.pathParam("id");
        int loanId;
        try {
            loanId = Integer.parseInt(loanIdParam);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Invalid loan ID.\"}");
            return;
        }

        // Change the loan status to "approved"
        boolean success = loanDao.updateLoanStatus(loanId, 2);

        if (success) {
            ctx.status(HttpStatus.OK).json("{\"message\":\"Loan approved successfully.\"}");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Failed to approve loan.\"}");
        }
    }

    public void rejectLoan(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to access this resource.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");

        if (user.getRoleId() != 1) {
            ctx.status(HttpStatus.FORBIDDEN).json("{\"error\":\"Unauthorized user.\"}");
            return;
        }

        String loanIdParam = ctx.pathParam("id");
        int loanId;
        try {
            loanId = Integer.parseInt(loanIdParam);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Invalid loan ID.\"}");
            return;
        }

        // // Change the loan status to "rejected"
        boolean success = loanDao.updateLoanStatus(loanId, 3);

        if (success) {
            ctx.status(HttpStatus.OK).json("{\"message\":\"Loan rejected successfully.\"}");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Failed to reject loan.\"}");
        }
    }

}