package repository.solr;

@org.springframework.stereotype.Repository
public interface ReportConfigOptionSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<
        models.ReportConfigOption, Long> {}
