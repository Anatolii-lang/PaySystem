package PaySystem.PaySystem.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
@Entity
@Data
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String service;  // Название услуги (например, Интернет)

    private double amount;   // Сумма к оплате

    @Column(name = "is_paid")  // Используем "is_paid", так как это имя столбца в базе
    private boolean paid;    // Статус оплаты

    private LocalDate periodStart; // Дата начала периода
    private LocalDate periodEnd;   // Дата конца периода

    private LocalDate createdAt = LocalDate.now(); // Когда счет был создан

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Внешний ключ на пользователя
    private User user;
}

