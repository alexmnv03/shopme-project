package com.shopme.backend.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.shopme.backend.repository.RoleRepository;
import com.shopme.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTests {

    private final static String ROLE_ADMIN = "Admin_test";
    private final static String ROLE_SALE = "Salesperson_test";
    private final static String ROLE_EDIT = "Editor_test";
    private final static String ROLE_SHIPPER = "Shipper_test";
    private final static String ROLE_ASSISTANT = "Assistant_test";

    private final static String EMAIL_FIRST = "y@a.net";
    private final static String EMAIL_UPDATE = "ya@a.ru";
    private final static String EMAIL_DELETE = "yandex@net";



    private final static Long ROLE_ID_01 = 1L;
	private UserRepository repo;
    private RoleRepository role;
	private TestEntityManager entityManager;

	@Autowired
	public UserRepositoryTests(UserRepository repo, RoleRepository role,
                               TestEntityManager entityManager) {
		super();
		this.repo = repo;
		this.role = role;
		this.entityManager = entityManager;
	}

    @BeforeAll
    public void createAllRoles() {
        Role roleAdmin = new Role(ROLE_ADMIN, "manage everything");
        Role savedRole = role.save(roleAdmin);
        Role roleSalesperson = new Role(ROLE_SALE, "manage product price, "
                + "customers, shipping, orders and sales report");
        Role roleEditor = new Role(ROLE_EDIT, "manage categories, brands, "
                + "products, articles and menus");
        Role roleShipper = new Role(ROLE_SHIPPER, "view products, view orders "
                + "and update order status");
        Role roleAssistant = new Role(ROLE_ASSISTANT, "manage questions and reviews");
        role.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));

        User userWithOneRole = new User(EMAIL_FIRST, "ya2020", "Remzi", "Akşaç");
        userWithOneRole.addRole(roleAdmin);
        repo.save(userWithOneRole);

    }
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1L);
		User userWithOneRole = new User("yndex@a.net", "ya2020", "Alex", "Akşaç");
		userWithOneRole.addRole(roleAdmin);

		User savedUser = repo.save(userWithOneRole);

		assertThat(savedUser.getId()).isGreaterThan(1);
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userWithTwoRole = new User("r@g.com", "rg2020", "Remzi", "Güloğlu");
		Role roleEditor = new Role(3L);
		Role roleAssistant = new Role(5L);
		
		userWithTwoRole.addRole(roleEditor);
		userWithTwoRole.addRole(roleAssistant);
		
		User savedUser = repo.save(userWithTwoRole);
		
		assertThat(savedUser.getId()).isGreaterThan(1);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User userById = repo.findById(ROLE_ID_01).get();
		System.out.println(userById);
		assertThat(userById).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userUpdateUserDetails = repo.findById(ROLE_ID_01).get();
		userUpdateUserDetails.setEnabled(true);
		userUpdateUserDetails.setEmail(EMAIL_UPDATE);
		
		repo.save(userUpdateUserDetails);
        var userUpdated = repo.getUserByEmail(EMAIL_UPDATE);
        assertThat(userUpdated).isNotNull();
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userUpdateUserRoles = repo.findById(1L).get();
		Role roleEditor = new Role(3L);
		Role roleSalesperson = new Role(2L);
		
		userUpdateUserRoles.getRoles().remove(roleEditor);
		userUpdateUserRoles.addRole(roleSalesperson);
		
		repo.save(userUpdateUserRoles);
        var result = repo.findById(1L).get().getRoles().contains(roleSalesperson);
        assertThat(result).isTrue();
	}
	
	@Test
	public void testDeleteUser() {

        Role roleAdmin = entityManager.find(Role.class, 1L);
        User user = new User(EMAIL_DELETE, "ya2020", "Alex", "Akşaç");
        user.addRole(roleAdmin);
        User savedUser = repo.save(user);

        var userUpdated = repo.getUserByEmail(EMAIL_DELETE);
        assertThat(userUpdated).isNotNull();

		Long userDeleteUser = 2L;
		repo.deleteById(userUpdated.getId());
        userUpdated = repo.getUserByEmail(EMAIL_DELETE);
        assertThat(userUpdated).isNull();
	}
	
	@Test
	public void testGetUserByEmail() {
		User userByEmail = repo.getUserByEmail(EMAIL_FIRST);
		
		assertThat(userByEmail).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Long id = 1L;
		Long countById = repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0L);
	}
	
	@Test
	public void testEnableUser() {
		Long id = 3L;
		repo.updateEnabledStatus(id, true);
		
	}
	
	@Test
	public void testDisableUser() {
        Long id = 3L;
		repo.updateEnabledStatus(id, false);
		
	}
	
	@Test
	public void testListFirstPage() {
        Role roleAdmin = entityManager.find(Role.class, 1L);
        User savedUser = new User(EMAIL_DELETE, "ya2020", "Alex", "Akşaç");
        savedUser.addRole(roleAdmin);
        repo.save(savedUser);

		int pageNumber = 0;
		int pageSize = 2;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);

		List<User> listUsers = page.getContent();

		listUsers.forEach(user -> System.out.println(user));

		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "Remzi";

		int pageNumber = 0;
		int pageSize = 4;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);

		List<User> listUsers = page.getContent();

		listUsers.forEach(user -> System.out.println(user));	

		assertThat(listUsers.size()).isGreaterThan(0);
	}
}
