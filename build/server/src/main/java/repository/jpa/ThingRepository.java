package repository.jpa;

import d3e.core.SchemaConstants;
import models.Thing;
import org.springframework.stereotype.Service;

@Service
public class ThingRepository extends AbstractD3ERepository<Thing> {
  public int getTypeIndex() {
    return SchemaConstants.Thing;
  }
}
