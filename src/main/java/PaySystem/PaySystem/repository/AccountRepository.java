package PaySystem.PaySystem.repository;

import PaySystem.PaySystem.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // Метод для поиска всех счетов по user_id
    List<Account> findByUserId(Long userId);

    Optional<Account> findByAccountNumber(Long accountNumber);


}
