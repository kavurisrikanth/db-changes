package d3e.core;

import models.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.EntityMutator;

@Service
public class ChangePasswordService {
  @Autowired private EntityMutator mutator;

  public void change(ChangePasswordRequest request) {
    if (request == null) {
      return;
    }
  }
}
