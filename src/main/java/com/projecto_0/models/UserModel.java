package com.projecto_0.models;

public class UserModel {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private int roleId;
    private String roleName;

    public UserModel() {}

    public UserModel(int id, String name, String lastName, String email, String password, int roleId/*, String roleName*/) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        //this.roleName = roleName;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() { return roleName; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) { this.roleName = roleName; }
}