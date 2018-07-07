package com.soze.idlekluch.user.repository;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.user.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<User> getAllUsers() {
    final Query query = em.createQuery("SELECT u FROM User u");
    return query.getResultList();
  }

  @Override
  public Optional<User> getUserById(final EntityUUID userId) {
    Objects.requireNonNull(userId);

    final Query query = em.createQuery("SELECT u FROM User u WHERE UPPER(u.userId) = :userId AND u.deleted = false");
    query.setParameter("userId", userId);

    try {
      return Optional.of((User) query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<User> getUserByUsername(final String username) {
    Objects.requireNonNull(username);

    final Query query = em.createQuery("SELECT u FROM User u WHERE UPPER(u.username) = :username AND u.deleted = false");
    query.setParameter("username", username.toUpperCase());

    try {
      return Optional.of((User) query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean usernameExists(final String username) {
    Objects.requireNonNull(username);

    final Query query = em.createQuery("SELECT u FROM User u where UPPER(u.username) = :username");
    query.setParameter("username", username.toUpperCase());
    return !query.setMaxResults(1).getResultList().isEmpty();
  }

  @Override
  @Transactional
  public void addUser(final User user) {
    Objects.requireNonNull(user);
    em.persist(user);
  }

  @Override
  @Transactional
  public void updateUser(final User user) {
    Objects.requireNonNull(user);
    em.merge(user);
  }

  @Override
  @Transactional
  public void deleteUser(final String username) {
    Objects.requireNonNull(username);
    User user = getUserByUsername(username).get();

    user.setDeleted(true);
    updateUser(user);
  }


}
