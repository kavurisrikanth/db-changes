package repository.solr;

@org.springframework.stereotype.Repository
public interface ThingSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.Thing, Long> {}
