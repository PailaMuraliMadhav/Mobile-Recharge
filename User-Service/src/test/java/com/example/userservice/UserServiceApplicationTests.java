package com.example.userservice;

import com.example.userservice.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
