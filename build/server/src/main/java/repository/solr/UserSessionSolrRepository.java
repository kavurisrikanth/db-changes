package repository.solr;

@org.springframework.stereotype.Repository
public interface UserSessionSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.UserSession, Long> {}
