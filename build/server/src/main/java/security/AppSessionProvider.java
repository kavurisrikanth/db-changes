package security;

import java.util.Collections;
import models.AnonymousUser;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import store.EntityMutator;

@Component
@Service
public class AppSessionProvider {
  @Autowired private EntityMutator mutator;
  @Autowired private JwtTokenUtil jwtTokenUtil;

  public void setUser(String type, long id, String session) {
    if (id == 0l) {
      return;
    }
    UserProxy proxy = new UserProxy(type, id, session);
    setUserProxy(proxy);
  }

  public String updateUser() {
    UserProxy user = getCurrentUserProxy();
    return null;
  }

  public User getCurrentUser() {
    UserProxy user = getCurrentUserProxy();
    if (user == null) return new AnonymousUser();
    if (user.user != null) return user.user;
    return null;
  }

  public void setToken(String token) {
    if (token == null) {
      return;
    }
    UserProxy proxy = jwtTokenUtil.validateToken(token);
    setUserProxy(proxy);
  }

  public void setUserProxy(UserProxy proxy) {
    if (proxy == null) {
      return;
    }
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(proxy, proxy.sessionId, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  public UserProxy getCurrentUserProxy() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null
        || auth
            instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
      return null;
    }
    Object principal = auth.getPrincipal();
    return ((UserProxy) principal);
  }

  public void clear() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}
