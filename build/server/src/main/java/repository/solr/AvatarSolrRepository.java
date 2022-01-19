package repository.solr;

@org.springframework.stereotype.Repository
public interface AvatarSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.Avatar, Long> {}
