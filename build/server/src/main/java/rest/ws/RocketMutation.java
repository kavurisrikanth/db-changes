package rest.ws;

import d3e.core.SchemaConstants;
import models.CreatableObject;
import models.User;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import store.EntityHelperService;

@org.springframework.stereotype.Service
public class RocketMutation extends AbstractRocketMutation {
  @Autowired private ObjectFactory<EntityHelperService> helperService;

  public boolean canCreate(User user, CreatableObject obj) {
    switch (user._typeIdx()) {
      case SchemaConstants.AnonymousUser:
        {
          switch (obj._typeIdx()) {
            case SchemaConstants.Thing:
              {
                return true;
              }
          }
        }
    }
    return false;
  }

  public boolean canUpdate(User user, CreatableObject obj) {
    switch (user._typeIdx()) {
      case SchemaConstants.AnonymousUser:
        {
          switch (obj._typeIdx()) {
            case SchemaConstants.Thing:
              {
                return true;
              }
          }
        }
    }
    return false;
  }

  public boolean canDelete(User user, CreatableObject obj) {
    switch (user._typeIdx()) {
      case SchemaConstants.AnonymousUser:
        {
          switch (obj._typeIdx()) {
            case SchemaConstants.Thing:
              {
                return true;
              }
          }
        }
    }
    return false;
  }
}
