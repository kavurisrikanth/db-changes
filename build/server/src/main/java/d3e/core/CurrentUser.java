package d3e.core;

import models.User;
import security.AppSessionProvider;

@org.springframework.stereotype.Service
public class CurrentUser implements org.springframework.beans.factory.InitializingBean {
  private static CurrentUser instance;

  @org.springframework.beans.factory.annotation.Autowired
  private AppSessionProvider sessionProvider;

  public static String updateUser() {
    return instance.sessionProvider.updateUser();
  }

  public static void set(String type, long id, String session) {
    instance.sessionProvider.setUser(type, id, session);
  }

  public static User get() {
    if (instance == null) {
      return null;
    }
    return ((User) instance.sessionProvider.getCurrentUser());
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

  public static void clear() {
    instance.sessionProvider.clear();
  }
}
