package com.mycompany.myapp.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String MANAGER = "ROLE_MANAGER";

    public static final String SUPMANAGER = "ROLE_SUPMANAGER";

    public static final String ADVISER = "ROLE_ADVISER";

    public static final String SUPADVISER = "ROLE_SUPADVISER";

    private AuthoritiesConstants() {}
}
