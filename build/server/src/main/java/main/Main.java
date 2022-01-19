package main;

import d3e.core.D3ELocalResourceHandler;
import d3e.core.D3ELogger;
import d3e.core.D3EResourceHandler;
import d3e.core.GraphQLFilter;
import d3e.core.TransactionWrapper;
import gqltosql.GqlToSql;
import gqltosql.schema.IModelSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import store.D3EEntityManagerProvider;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@ComponentScan({
  "changestest",
  "classes",
  "graphql",
  "helpers",
  "models",
  "repository",
  "security",
  "storage",
  "d3e.core",
  "store",
  "parser",
  "rest",
  "lists"
})
@EnableSolrRepositories("repository.solr")
public class Main {
  @Autowired private D3EEntityManagerProvider em;

  @Value("${d3e.showSql:false}")
  private boolean showSql;

  @Value("${d3e.showGraphql:false}")
  private boolean showGraphql;

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  public FilterRegistrationBean<GraphQLFilter> graphQLFilter(TransactionWrapper wrapper) {
    FilterRegistrationBean<GraphQLFilter> registrationBean =
        new FilterRegistrationBean<GraphQLFilter>();
    registrationBean.setFilter(new GraphQLFilter(wrapper));
    registrationBean.addUrlPatterns("/api/native/graphql");
    return registrationBean;
  }

  @Bean
  public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(327680);
    container.setMaxBinaryMessageBufferSize(327680);
    return container;
  }

  @Bean
  @Primary
  public D3EResourceHandler getResourceHandler() {
    return new D3ELocalResourceHandler();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public GqlToSql gqlToSql(IModelSchema schema) {
    return new GqlToSql(null, schema);
  }

  @Bean
  public gqltosql2.GqlToSql gqlToSql2(IModelSchema schema) {
    return new gqltosql2.GqlToSql(em, schema);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void loadQueryLogger() {
    D3ELogger.setShowSql(showSql);
    D3ELogger.setShowGraphql(showGraphql);
  }
}
