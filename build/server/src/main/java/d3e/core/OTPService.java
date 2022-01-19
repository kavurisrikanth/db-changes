package d3e.core;

import javax.annotation.PostConstruct;
import models.OneTimePassword;
import org.springframework.stereotype.Service;

@Service
public class OTPService {
  private static OTPService instance;

  @PostConstruct
  public void init() {
    instance = this;
  }

  public static OTPService get() {
    return instance;
  }

  public void create(OneTimePassword otp) {}
}
