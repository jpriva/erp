package com.jpriva.erpsp.notification.infra.out.persistence.adapter;

import com.jpriva.erpsp.notification.domain.model.Notification;
import com.jpriva.erpsp.notification.domain.model.NotificationStatus;
import com.jpriva.erpsp.notification.domain.port.out.NotificationRepositoryPort;
import com.jpriva.erpsp.notification.infra.out.persistence.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        return NotificationMapper.toDomain(
                jpaRepository.save(NotificationMapper.toEntity(notification))
        );
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(NotificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByNotSent() {
        return jpaRepository.findByStatusIsNot(NotificationStatus.SENT).stream().map(NotificationMapper::toDomain).toList();
    }
}
