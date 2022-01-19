package d3e.core;

import classes.LoginResult;
import javax.annotation.PostConstruct;
import models.AnonymousUser;
import models.OneTimePassword;
import models.RefModel;
import models.Thing;
import models.User;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.AvatarRepository;
import repository.jpa.ChildModelRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.RefModelRepository;
import repository.jpa.ReportConfigOptionRepository;
import repository.jpa.ReportConfigRepository;
import repository.jpa.ThingRepository;
import repository.jpa.UserRepository;
import repository.jpa.UserSessionRepository;
import security.AppSessionProvider;
import security.JwtTokenUtil;

@Service
public class QueryProvider {
  public static QueryProvider instance;
  @Autowired private JwtTokenUtil jwtTokenUtil;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private AvatarRepository avatarRepository;
  @Autowired private ChildModelRepository childModelRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private RefModelRepository refModelRepository;
  @Autowired private ReportConfigRepository reportConfigRepository;
  @Autowired private ReportConfigOptionRepository reportConfigOptionRepository;
  @Autowired private ThingRepository thingRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private ObjectFactory<AppSessionProvider> provider;

  @PostConstruct
  public void init() {
    instance = this;
  }

  public static QueryProvider get() {
    return instance;
  }

  public AnonymousUser getAnonymousUserById(long id) {
    return anonymousUserRepository.findById(id);
  }

  public OneTimePassword getOneTimePasswordById(long id) {
    return oneTimePasswordRepository.findById(id);
  }

  public boolean checkTokenUniqueInOneTimePassword(long oneTimePasswordId, String token) {
    return oneTimePasswordRepository.checkTokenUnique(oneTimePasswordId, token);
  }

  public RefModel getRefModelById(long id) {
    return refModelRepository.findById(id);
  }

  public Thing getThingById(long id) {
    return thingRepository.findById(id);
  }

  public LoginResult loginWithOTP(String token, String code, String deviceToken) {
    OneTimePassword otp = oneTimePasswordRepository.getByToken(token);
    User user = otp.getUser();
    LoginResult loginResult = new LoginResult();
    if (deviceToken != null) {
      user.setDeviceToken(deviceToken);
    }
    loginResult.setSuccess(true);
    loginResult.setUserObject(otp.getUser());
    loginResult.setToken(token);
    return loginResult;
  }
}
