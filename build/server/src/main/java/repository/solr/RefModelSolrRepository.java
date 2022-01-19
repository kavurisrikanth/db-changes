package repository.solr;

@org.springframework.stereotype.Repository
public interface RefModelSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.RefModel, Long> {}
