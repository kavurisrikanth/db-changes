package repository.jpa;

import d3e.core.SchemaConstants;
import models.ChildModel;
import org.springframework.stereotype.Service;

@Service
public class ChildModelRepository extends AbstractD3ERepository<ChildModel> {
  public int getTypeIndex() {
    return SchemaConstants.ChildModel;
  }
}
