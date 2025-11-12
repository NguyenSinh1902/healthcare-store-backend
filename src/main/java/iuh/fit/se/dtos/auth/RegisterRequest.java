package iuh.fit.se.dtos.auth;

import iuh.fit.se.entities.auth.Role;

public class RegisterRequest {

    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;

    public RegisterRequest() {
        // mac dinh la USER neu khong truyen role
        this.role = Role.USER;
    }

    //GETTERS & SETTERS

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        // Neu role null mac dinh la USER
        if (role == null) {
            this.role = Role.USER;
        } else {
            this.role = role;
        }
    }
}
