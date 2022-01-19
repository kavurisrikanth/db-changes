package repository.jpa;

import d3e.core.SchemaConstants;
import models.User;
import org.springframework.stereotype.Service;

@Service
public class UserRepository extends AbstractD3ERepository<User> {
  public int getTypeIndex() {
    return SchemaConstants.User;
  }
}
