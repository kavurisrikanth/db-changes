package repository.jpa;

import d3e.core.SchemaConstants;
import models.Avatar;
import org.springframework.stereotype.Service;

@Service
public class AvatarRepository extends AbstractD3ERepository<Avatar> {
  public int getTypeIndex() {
    return SchemaConstants.Avatar;
  }
}
