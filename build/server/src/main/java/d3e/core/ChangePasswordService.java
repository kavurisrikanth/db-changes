package d3e.core;

import classes.MutateResultStatus;
import models.AnonymousUser;
import models.ChangePasswordRequest;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.EntityMutator;
import store.ValidationFailedException;

@Service
public class ChangePasswordService {
  @Autowired private EntityMutator mutator;

  public void change(ChangePasswordRequest request) {
    if (request == null) {
      return;
    }
    User currentUser = CurrentUser.get();
    if (currentUser == null || !(currentUser instanceof AnonymousUser)) {
      throw new ValidationFailedException(
          MutateResultStatus.AuthFail,
          ListExt.asList("Invalid change password request for current user."));
    }
    String password = request.getNewPassword();
  }
}
