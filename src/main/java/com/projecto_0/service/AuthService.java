package com.projecto_0.service;

import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public String updatePassword(String newPlainPassword) {
        return hashPassword(newPlainPassword);
    }
}
