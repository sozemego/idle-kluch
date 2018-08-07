package com.soze.idlekluch.game.upgrade.repository;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.jpa.QueryUtils;
import com.soze.idlekluch.game.upgrade.entity.UpgradeEntity;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public class UpgradeRepositoryImpl implements UpgradeRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public int getUpgradeLevel(final EntityUUID entityId, final UpgradeType upgradeType) {
    final Query query = em.createQuery("SELECT u FROM UpgradeEntity u WHERE u.upgradeType = :upgradeType AND u.entityId = :entityId");
    query.setParameter("upgradeType", upgradeType.toString());
    query.setParameter("entityId", entityId);
    query.setMaxResults(1);
    final Optional<UpgradeEntity> upgrade = QueryUtils.getOptional(query, UpgradeEntity.class);
    if (upgrade.isPresent()) {
      return upgrade.get().getLevel();
    }
    return 1;
  }

  @Override
  @Transactional
  public void increaseUpgradeLevel(final EntityUUID entityId, final UpgradeType upgradeType) {
    final Query query = em.createQuery("SELECT u FROM UpgradeEntity u WHERE u.upgradeType = :upgradeType AND u.entityId = :entityId");
    query.setParameter("upgradeType", upgradeType.toString());
    query.setParameter("entityId", entityId);
    final Optional<UpgradeEntity> upgrade = QueryUtils.getOptional(query, UpgradeEntity.class);

    if(upgrade.isPresent()) {
      final UpgradeEntity upgradeEntity = upgrade.get();
      upgradeEntity.setLevel(upgradeEntity.getLevel() + 1);
      em.merge(upgradeEntity);
    } else {
      final UpgradeEntity upgradeEntity = new UpgradeEntity();
      upgradeEntity.setLevel(1);
      upgradeEntity.setEntityId(entityId);
      upgradeEntity.setUpgradeType(upgradeType.toString());
      em.persist(upgradeEntity);
    }
  }

}
