package repository.jpa;

import d3e.core.SchemaConstants;
import models.ReportConfigOption;
import org.springframework.stereotype.Service;

@Service
public class ReportConfigOptionRepository extends AbstractD3ERepository<ReportConfigOption> {
  public int getTypeIndex() {
    return SchemaConstants.ReportConfigOption;
  }
}
