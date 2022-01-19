package repository.jpa;

import d3e.core.SchemaConstants;
import models.AnonymousUser;
import org.springframework.stereotype.Service;

@Service
public class AnonymousUserRepository extends AbstractD3ERepository<AnonymousUser> {
  public int getTypeIndex() {
    return SchemaConstants.AnonymousUser;
  }
}
