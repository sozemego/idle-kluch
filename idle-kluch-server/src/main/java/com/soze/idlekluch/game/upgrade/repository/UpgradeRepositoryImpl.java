package com.soze.idlekluch.game.upgrade.repository;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.jpa.QueryUtils;
import com.soze.idlekluch.game.upgrade.entity.UpgradeEntity;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;

@Repository
public class UpgradeRepositoryImpl implements UpgradeRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public int getUpgradeLevel(final EntityUUID entityId, final UpgradeType upgradeType) {
    final Query query = em.createQuery("SELECT u FROM UpgradeEntity u WHERE u.upgradeType = :upgradeType AND u.entity");
    final Optional<UpgradeEntity> upgrade = QueryUtils.getOptional(query, UpgradeEntity.class);
    if (upgrade.isPresent()) {
      return upgrade.get().getLevel();
    }
    return 1;
  }

}
