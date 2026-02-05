package com.jpriva.erpsp.notification.infra.out.persistence.adapter;

import com.jpriva.erpsp.notification.domain.model.NotificationStatus;
import com.jpriva.erpsp.notification.infra.out.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByStatusIsNot(NotificationStatus status);
}
