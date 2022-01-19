package repository.solr;

@org.springframework.stereotype.Repository
public interface UserSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.User, Long> {}
