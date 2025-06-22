package com.dev.focusshield;

import com.dev.focusshield.config.JwtTokenProvider;
import com.dev.focusshield.config.SecurityConfig; // Import SecurityConfig
import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {FocusshieldApplication.class, SecurityConfig.class})
@ActiveProfiles("test")
public class AdminAccessTest {

    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())  // <--- important
                .build();
    }

    @Test
    public void testAccessDeniedForNonAdminUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("normaluser");
        user.setEmail("normal@example.com");

        RoleEntity userRole = new RoleEntity();
        userRole.setRoleName("ROLE_USER");

        user.setRoles(List.of(userRole));

        String token = jwtTokenProvider.generateToken(user);

        mockMvc.perform(get("/admin/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAccessGrantedForAdminUser() throws Exception {
        UserEntity admin = new UserEntity();
        admin.setUsername("adminuser");
        admin.setEmail("admin@example.com");

        RoleEntity adminRole = new RoleEntity();
        adminRole.setRoleName("ROLE_ADMIN");

        admin.setRoles(List.of(adminRole));

        String token = jwtTokenProvider.generateToken(admin);

        mockMvc.perform(get("/admin/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Admin!"));
    }
}