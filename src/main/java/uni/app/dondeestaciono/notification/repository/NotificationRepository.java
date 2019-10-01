package uni.app.dondeestaciono.notification.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import uni.app.dondeestaciono.notification.model.PhoneRegistration;

public interface NotificationRepository
    extends ReactiveCrudRepository<PhoneRegistration, String> {}
