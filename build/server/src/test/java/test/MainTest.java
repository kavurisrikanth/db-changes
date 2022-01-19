package test;

import main.Main;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
@WebAppConfiguration
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:~/test;MODE=PostgreSQL",
      "spring.profiles.active=test"
    })
public class MainTest {
  @Autowired ApplicationContext context;

  @Test
  public void contextLoads() {
    Assert.assertTrue("Startup failed", context != null);
  }
}
