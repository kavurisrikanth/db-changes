package repository.jpa;

import d3e.core.SchemaConstants;
import models.UserSession;
import org.springframework.stereotype.Service;

@Service
public class UserSessionRepository extends AbstractD3ERepository<UserSession> {
  public int getTypeIndex() {
    return SchemaConstants.UserSession;
  }
}
