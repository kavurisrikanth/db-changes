package rest.ws;

import models.CreatableObject;
import models.User;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import store.EntityHelperService;

@org.springframework.stereotype.Service
public class RocketMutation extends AbstractRocketMutation {
  @Autowired private ObjectFactory<EntityHelperService> helperService;

  public boolean canCreate(User user, CreatableObject obj) {
    return false;
  }

  public boolean canUpdate(User user, CreatableObject obj) {
    return false;
  }

  public boolean canDelete(User user, CreatableObject obj) {
    return false;
  }
}
