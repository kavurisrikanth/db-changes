package repository.jpa;

import d3e.core.SchemaConstants;
import models.ReportConfig;
import org.springframework.stereotype.Service;

@Service
public class ReportConfigRepository extends AbstractD3ERepository<ReportConfig> {
  public int getTypeIndex() {
    return SchemaConstants.ReportConfig;
  }
}
