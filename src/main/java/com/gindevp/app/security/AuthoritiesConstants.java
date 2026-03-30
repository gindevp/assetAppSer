package com.gindevp.app.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String ASSET_MANAGER = "ROLE_ASSET_MANAGER";

    public static final String DEPARTMENT_COORDINATOR = "ROLE_DEPARTMENT_COORDINATOR";

    public static final String EMPLOYEE = "ROLE_EMPLOYEE";

    /** Giám đốc — quyền xem/duyệt cấp QLTS (Phase 1) */
    public static final String GD = "ROLE_GD";

    private AuthoritiesConstants() {}
}
