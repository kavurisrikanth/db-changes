package repository.solr;

@org.springframework.stereotype.Repository
public interface ChildModelSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.ChildModel, Long> {}
