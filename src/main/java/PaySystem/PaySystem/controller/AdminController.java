package PaySystem.PaySystem.controller;

import PaySystem.PaySystem.model.Account;
import PaySystem.PaySystem.model.Bill;
import PaySystem.PaySystem.model.User;
import PaySystem.PaySystem.repository.AccountRepository;
import PaySystem.PaySystem.repository.BillRepository;
import PaySystem.PaySystem.repository.UserRepository;
import PaySystem.PaySystem.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin-page")
public class AdminController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BillRepository billRepository;

    // Показ всех пользователей (кроме администраторов)
    @GetMapping
    public String showAdminPage(Model model) {
        List<User> users = userRepository.findByRoleNot("admin");
        model.addAttribute("users", users);
        return "admin-page"; // Убедись, что шаблон существует
    }

    // Показ счетов пользователя
    @GetMapping("/user/{userId}")
    public String showUserAccounts(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/admin-page";
        }
        List<Account> accounts = accountRepository.findByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        return "user-accounts"; // Убедись, что шаблон существует
    }

    // Обновление статуса счета (активный/неактивный)
    @GetMapping("/account/update-status/{accountId}")
    public String updateAccountStatus(@PathVariable Long accountId, @RequestParam String status) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account != null) {
            account.setActive("active".equals(status));
            accountRepository.save(account);
            return "redirect:/admin-page/user/" + account.getUserId();
        }
        return "redirect:/admin-page";
    }




    // Показ формы для выставления счета
    @GetMapping("/create-bill/{userId}")
    public String showCreateBillPage(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/admin-page";
        }
        model.addAttribute("user", user);
        return "create-bill";
    }

    @PostMapping("/create-bill")
    public String createBill(@RequestParam Long userId,
                             @RequestParam String service,
                             @RequestParam double amount,
                             @RequestParam String periodStart,
                             @RequestParam String periodEnd,
                             Model model) {
        // Проверка существования пользователя
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Пользователь не найден");
            return "create-bill"; // если пользователь не найден, возвращаем обратно на форму
        }

        // Создание нового счета
        Bill bill = new Bill();
        bill.setUser(user);
        bill.setService(service);
        bill.setAmount(amount);
        bill.setPeriodStart(LocalDate.parse(periodStart));
        bill.setPeriodEnd(LocalDate.parse(periodEnd));
        bill.setPaid(false);
        bill.setCreatedAt(LocalDate.now());

        // Сохраняем счет
        billRepository.save(bill);

        // После сохранения — редирект на страницу счетов пользователя
        return "redirect:/admin-page/user-bills/" + userId;
    }



    @GetMapping("/user-bills/{userId}")
    public String showUserBills(@PathVariable Long userId, Model model) {
        // Получаем пользователя по userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/admin-page"; // Если пользователь не найден, перенаправляем на панель администратора
        }

        // Получаем все счета, выставленные данному пользователю
        List<Bill> bills = billRepository.findByUserId(userId);

        // Добавляем пользователя и счета в модель
        model.addAttribute("user", user);
        model.addAttribute("bills", bills);

        // Возвращаем страницу с отображением счетов
        return "user-bills";
    }






}
