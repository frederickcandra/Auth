package com.jwt.auth.request;

import com.jwt.auth.model.Role;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    private String username;
    private Role role;
}
