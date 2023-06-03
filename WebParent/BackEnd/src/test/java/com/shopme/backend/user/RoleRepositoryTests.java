package com.shopme.backend.user;

import com.shopme.backend.repository.RoleRepository;
import com.shopme.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleRepositoryTests {

    private final static String ROLE_ADMIN = "Admin_test";
    private final static String ROLE_SALE = "Salesperson_test";
    private final static String ROLE_EDIT = "Editor_test";
    private final static String ROLE_SHIPPER = "Shipper_test";
    private final static String ROLE_ASSISTANT = "Assistant_test";

    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole() {

        Role roleAdmin = new Role(ROLE_ADMIN, "manage everything");
        Role savedRole = repo.save(roleAdmin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = new Role(ROLE_SALE, "manage product price, "
                + "customers, shipping, orders and sales report");

        Role roleEditor = new Role(ROLE_EDIT, "manage categories, brands, "
                + "products, articles and menus");

        Role roleShipper = new Role(ROLE_SHIPPER, "view products, view orders "
                + "and update order status");

        Role roleAssistant = new Role(ROLE_ASSISTANT, "manage questions and reviews");

        repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
    }

}
