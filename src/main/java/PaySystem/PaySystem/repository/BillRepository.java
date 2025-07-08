package PaySystem.PaySystem.repository;

import PaySystem.PaySystem.model.Bill;
import PaySystem.PaySystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    // Здесь можно добавить методы для поиска счетов по пользователю и т.д.
    List<Bill> findByUserId(Long userId);
    List<Bill> findByUser(User user); // Получаем все счета для конкретного пользователя

}
