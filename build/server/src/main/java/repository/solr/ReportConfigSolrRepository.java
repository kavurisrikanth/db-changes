package repository.solr;

@org.springframework.stereotype.Repository
public interface ReportConfigSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<
        models.ReportConfig, Long> {}
