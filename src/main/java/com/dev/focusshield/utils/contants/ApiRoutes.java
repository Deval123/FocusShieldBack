package com.dev.focusshield.utils.contants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
abstract public class ApiRoutes {
    public static final  String ROLES = "/roles";
    public static final  String USERS = "/users";
    public static final  String FOCUS_CONFIG = "/focus-config";
    public static final  String USERS_ASSIGN_ROLE = "/assign-role";
    public static final  String USERS_UNASSIGN_ROLE = "/unassign-role";

}
