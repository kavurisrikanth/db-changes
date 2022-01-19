package repository.jpa;

import d3e.core.SchemaConstants;
import models.RefModel;
import org.springframework.stereotype.Service;

@Service
public class RefModelRepository extends AbstractD3ERepository<RefModel> {
  public int getTypeIndex() {
    return SchemaConstants.RefModel;
  }
}
