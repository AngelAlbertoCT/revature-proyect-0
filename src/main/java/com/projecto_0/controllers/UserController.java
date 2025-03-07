package com.projecto_0.controllers;
import com.projecto_0.dao.UserDao;
import com.projecto_0.models.UserModel;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public class UserController {
    private UserDao userDao = new UserDao();

    public void updateInfo(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to update your information.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");
        String email = user.getEmail();

        Map<String, String> updateData = ctx.bodyAsClass(Map.class);

        String field = updateData.get("field");
        String newValue = updateData.get("value");

        if (field == null || newValue == null) {
            ctx.status(HttpStatus.BAD_REQUEST).json("{\"error\":\"Parameters 'field' and 'value' are required.\"}");
            return;
        }

        // Update user information
        boolean success = userDao.updateUserInfo(email, field, newValue);

        if (success) {
            ctx.status(HttpStatus.OK).json("{\"message\":\"Information updated successfully.\"}");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json("{\"error\":\"Error updating information.\"}");
        }
    }

    public void getUserInfo(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json("{\"error\":\"You must be logged in to access this resource.\"}");
            return;
        }

        UserModel user = (UserModel) session.getAttribute("user");

        List<Map<String, Object>> userInfoList;

        if (user.getRoleId() == 1) {
            // If admin get all information for all users
            userInfoList = userDao.getAllUserInfo();
        } else if (user.getRoleId() == 2) {
            // If user is normal get only his information
            userInfoList = userDao.getUserInfoById(user.getId());
        } else {
            ctx.status(HttpStatus.FORBIDDEN).json("{\"error\":\"Unauthorized user.\"}");
            return;
        }

        // Check if the user has no registered loans
        if (userInfoList.isEmpty()) {
            ctx.status(HttpStatus.OK).json("{\"message\":\"You have no registered loan applications.\"}");
            return;
        }

        ctx.json(userInfoList);
    }
}
