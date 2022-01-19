package rest.ws;

import classes.LoginResult;
import d3e.core.CurrentUser;
import d3e.core.D3ELogger;
import d3e.core.ListExt;
import gqltosql2.Field;
import gqltosql2.GqlToSql;
import gqltosql2.OutObject;
import java.util.UUID;
import models.OneTimePassword;
import models.User;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.OneTimePasswordRepository;
import security.AppSessionProvider;
import security.JwtTokenUtil;
import security.UserProxy;
import store.ValidationFailedException;

@Service
public class RocketQuery extends AbstractRocketQuery {
  @Autowired private GqlToSql gqlToSql;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ObjectFactory<AppSessionProvider> provider;
  @Autowired private JwtTokenUtil jwtTokenUtil;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private DataChangeTracker dataChangeTracker;

  protected LoginResult login(
      String query,
      String email,
      String phone,
      String username,
      String password,
      String deviceToken,
      String token,
      String code)
      throws Exception {
    D3ELogger.displayGraphQL(query, query, null);
    switch (query) {
      case "loginWithOTP":
        {
          return loginWithOTP(token, code, deviceToken);
        }
    }
    D3ELogger.info("Query Not found");
    return null;
  }

  protected QueryResult executeOperation(
      String query, Field field, RocketInputContext ctx, boolean subscribed, ClientSession session)
      throws Exception {
    D3ELogger.displayGraphQL(query, query, null);
    User currentUser = CurrentUser.get();
    switch (query) {
      case "getAnonymousUserById":
        {
          OutObject one = gqlToSql.execute("AnonymousUser", field, ctx.readLong());
          if (subscribed && one != null) {
            OutObjectTracker tracker = new OutObjectTracker(dataChangeTracker, session, field);
            tracker.init(one);
            return singleResult("AnonymousUser", false, one, tracker);
          }
          return singleResult("AnonymousUser", false, one);
        }
      case "getOneTimePasswordById":
        {
          OutObject one = gqlToSql.execute("OneTimePassword", field, ctx.readLong());
          if (subscribed && one != null) {
            OutObjectTracker tracker = new OutObjectTracker(dataChangeTracker, session, field);
            tracker.init(one);
            return singleResult("OneTimePassword", false, one, tracker);
          }
          return singleResult("OneTimePassword", false, one);
        }
      case "getRefModelById":
        {
          OutObject one = gqlToSql.execute("RefModel", field, ctx.readLong());
          if (subscribed && one != null) {
            OutObjectTracker tracker = new OutObjectTracker(dataChangeTracker, session, field);
            tracker.init(one);
            return singleResult("RefModel", false, one, tracker);
          }
          return singleResult("RefModel", false, one);
        }
      case "getThingById":
        {
          OutObject one = gqlToSql.execute("Thing", field, ctx.readLong());
          if (subscribed && one != null) {
            OutObjectTracker tracker = new OutObjectTracker(dataChangeTracker, session, field);
            tracker.init(one);
            return singleResult("Thing", false, one, tracker);
          }
          return singleResult("Thing", false, one);
        }
      case "currentAnonymousUser":
        {
          return singleResult("AnonymousUser", false, provider.getObject().getAnonymousUser());
        }
    }
    D3ELogger.info("Query Not found");
    throw new ValidationFailedException(ListExt.asList("Invalid Query: " + query));
  }

  private LoginResult loginWithOTP(String token, String code, String deviceToken) throws Exception {
    OneTimePassword otp = oneTimePasswordRepository.getByToken(token);
    LoginResult loginResult = new LoginResult();
    if (otp == null) {
      loginResult.setSuccess(false);
      loginResult.setFailureMessage("Invalid token.");
      return loginResult;
    }
    if (otp.getExpiry().isBefore(java.time.LocalDateTime.now())) {
      loginResult.setSuccess(false);
      loginResult.setFailureMessage("OTP validity has expired.");
      return loginResult;
    }
    if (!(code.equals(otp.getCode()))) {
      loginResult.setSuccess(false);
      loginResult.setFailureMessage("Invalid code.");
      return loginResult;
    }
    User user = otp.getUser();
    if (user == null) {
      loginResult.setSuccess(false);
      loginResult.setFailureMessage("Invalid user.");
      return loginResult;
    }
    loginResult.setSuccess(true);
    loginResult.setUserObject(user);
    String type = ((String) user.getClass().getSimpleName());
    String id = String.valueOf(user.getId());
    String finalToken =
        jwtTokenUtil.generateToken(
            id, new UserProxy(type, user.getId(), UUID.randomUUID().toString()));
    loginResult.setToken(finalToken);
    if (deviceToken != null) {
      user.setDeviceToken(deviceToken);
      store.Database.get().save(user);
    }
    return loginResult;
  }
}
