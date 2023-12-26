package com.example.dwbug.en;



public enum RoleEnum {
    ROLE_ENUM(0L,"超级管理员"),
    ROLE_ENUM1(1L,"管理员"),
    ROLE_ENUM2(2L,"普通用户");

    private Long roleId;
    private String role;

    RoleEnum(Long roleId, String role) {
        this.roleId = roleId;
        this.role = role;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
