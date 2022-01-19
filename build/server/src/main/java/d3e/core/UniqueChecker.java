package d3e.core;

import models.OneTimePassword;

public class UniqueChecker {
  public static boolean checkTokenUniqueInOneTimePassword(
      OneTimePassword oneTimePassword, String token) {
    return QueryProvider.get().checkTokenUniqueInOneTimePassword(oneTimePassword.getId(), token);
  }
}
