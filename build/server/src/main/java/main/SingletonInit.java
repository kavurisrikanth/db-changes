package main;

import d3e.core.QueryProvider;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import store.EntityMutator;

@Component
public class SingletonInit {
  @Autowired private QueryProvider queryProvider;
  @Autowired private EntityMutator mutator;

  @PostConstruct
  public void initialize() {
    QueryProvider.instance = queryProvider;
  }
}
