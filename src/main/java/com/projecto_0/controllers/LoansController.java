package com.projecto_0.controllers;
import com.projecto_0.dao.LoanDao;
import com.projecto_0.models.LoanModel;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
import com.projecto_0.models.UserModel;
import java.util.Map;

public class LoansController {

    LoanDao loanDao = new LoanDao();

    public void requestLoan(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to request a loan.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");
        int userId = user.getId();

        LoanModel loanRequest = ctx.bodyAsClass(LoanModel.class);

        // Validate loan data
        if (loanRequest.getLoanTypeId() == 0 || loanRequest.getAmount() == 0 || loanRequest.getStatusId() == 0) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Missing required fields: loan_type_id, amount, status_id.\"}");
            return;
        }

        // Create the loan in the database
        LoanModel loan = new LoanModel();
        loan.setUserId(userId);
        loan.setLoanTypeId(loanRequest.getLoanTypeId());
        loan.setAmount(loanRequest.getAmount());
        loan.setStatusId(loanRequest.getStatusId());

        boolean success = loanDao.createLoan(loan);

        if (success) {
            ctx.status(HttpStatus.CREATED).json("{\"message\":\"Loan requested successfully.\"}");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Failed to request loan.\"}");
        }
    }

    public void getLoanById(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to access this resource.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");

        String loanIdParam = ctx.pathParam("id");
        int loanId;
        try {
            loanId = Integer.parseInt(loanIdParam);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Invalid loan ID.\"}");
            return;
        }

        // Get loan details by ID
        Map<String, Object> loanDetails = loanDao.getLoanById(loanId);

        // Check if the loan exists
        if (loanDetails == null) {
            ctx.status(HttpStatus.NOT_FOUND).json("{\"error\":\"Loan not found.\"}");
            return;
        }

        // Check user permissions
        if (user.getRoleId() == 2 && (int) loanDetails.get("user_id") != user.getId()) {
            // If the user is regular and is not the owner of the loan
            ctx.status(HttpStatus.FORBIDDEN).json("{\"error\":\"Unauthorized access to this loan.\"}");
            return;
        }

        ctx.json(loanDetails);
    }

    public void updateLoan(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to access this resource.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");

        String loanIdParam = ctx.pathParam("id");
        int loanId;
        try {
            loanId = Integer.parseInt(loanIdParam);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Invalid loan ID.\"}");
            return;
        }

        // Get the data to update from the request body (JSON)
        Map<String, Object> updateData = ctx.bodyAsClass(Map.class);

        if (!updateData.containsKey("loan_type_id") || !updateData.containsKey("amount")) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Missing required fields: loan_type_id, amount.\"}");
            return;
        }

        Map<String, Object> loanDetails = loanDao.getLoanById(loanId);

        if (loanDetails == null) {
            ctx.status(HttpStatus.NOT_FOUND).json("{\"error\":\"Loan not found.\"}");
            return;
        }

        if (user.getRoleId() != 1 && (int) loanDetails.get("user_id") != user.getId()) {
            ctx.status(HttpStatus.FORBIDDEN).json("{\"error\":\"Unauthorized access to this loan.\"}");
            return;
        }

        // Update the loan
        int loanTypeId = (int) updateData.get("loan_type_id");
        double amount = (double) updateData.get("amount");

        boolean success = loanDao.updateLoan(loanId, loanTypeId, amount);

        if (success) {
            ctx.status(HttpStatus.OK).json("{\"message\":\"Loan updated successfully.\"}");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Failed to update loan.\"}");
        }
    }

}
