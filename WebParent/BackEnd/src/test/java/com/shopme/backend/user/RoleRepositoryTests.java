package com.shopme.backend.user;

import com.shopme.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
// Если мы хотим тестить с реальной БД
@AutoConfigureTestDatabase(replace = Replace.NONE)
// Если не хотим откатить транзакции, иначе транзакция будет откачена будет
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleRepositoryTests {

    private final static String ROLE_ADMIN = "Admin_test";
    private final static String ROLE_SALE = "Salesperson_test";
    private final static String ROLE_EDIT = "Editor_test";
    private final static String ROLE_SHIPPER = "Shipper_test";
    private final static String ROLE_ASSISTANT = "Assistant_test";

	@Autowired
	private RoleRepository repo;

    @BeforeAll
    public void deleteAllRoles() {
        deleteRole(ROLE_ADMIN);
        deleteRole(ROLE_SALE);
        deleteRole(ROLE_EDIT);
        deleteRole(ROLE_SHIPPER);
        deleteRole(ROLE_ASSISTANT);
    }

    public void deleteRole(String name){
        var result = repo.findByName(name);
        result.ifPresent(role -> repo.delete(role));
    }


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
