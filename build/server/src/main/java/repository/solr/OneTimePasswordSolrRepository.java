package repository.solr;

@org.springframework.stereotype.Repository
public interface OneTimePasswordSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<
        models.OneTimePassword, Long> {}
