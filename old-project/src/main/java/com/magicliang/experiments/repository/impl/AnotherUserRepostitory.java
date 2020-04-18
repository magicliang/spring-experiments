package com.magicliang.experiments.repository.impl;

import com.magicliang.experiments.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by magicliang on 2016/6/9.
 */

//A basic dao example.
//Don't need to aquire and release the entity manager
//http://stackoverflow.com/questions/30753579/spring-bootjpa-entitymanager-inject-fails
//See this to check the configuration detail
@Repository
public class AnotherUserRepostitory {
    //Only one persistenceContext can be injected.
//    @PersistenceUnit
    private EntityManagerFactory emf;
    //This is transactional entitymanager proxy

    /**
     * See: http://docs.spring.io/spring/docs/4.3.1.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/
     * <p>
     * The main problem with such a DAO is that it always creates a new EntityManager through the factory. You can avoid this by requesting a transactional EntityManager (also called "shared EntityManager" because it is a shared, thread-safe proxy for the actual transactional EntityManager) to be injected instead of the factory:
     * <p>
     * public class ProductDaoImpl implements ProductDao {
     * <p>
     * //    @PersistenceContext
     * private EntityManager em;
     * <p>
     * public Collection loadProductsByCategory(String category) {
     * Query query = em.createQuery("from Product as p where p.category = :category");
     * query.setParameter("category", category);
     * return query.getResultList();
     * }
     * }
     **/
//    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Cacheable("users")
    @Async
    public Future<List<User>> findByName(String name) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query q = entityManager.createQuery("select u from User u where u.name = :name");
        q.setParameter("name", name);
        List<User> users = q.getResultList();
        transaction.commit();
        //entityManager.close();
        return new AsyncResult<List<User>>(users);
    }

}
