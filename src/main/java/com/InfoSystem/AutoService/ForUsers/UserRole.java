package com.InfoSystem.AutoService.ForUsers;

//  Вспомогательный класс - enum для назначения ролей пользователей
public enum UserRole {
    CLIENT("CLIENT"),
    OPERATOR("OPERATOR"),
    MANAGER("MANAGER");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
