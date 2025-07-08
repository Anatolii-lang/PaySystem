package PaySystem.PaySystem.controller;

import PaySystem.PaySystem.model.Account;
import PaySystem.PaySystem.model.User;
import PaySystem.PaySystem.repository.AccountRepository;
import PaySystem.PaySystem.repository.UserRepository;
import PaySystem.PaySystem.service.AccountNumberGenerator;
import PaySystem.PaySystem.service.CustomUserDetails;
import PaySystem.PaySystem.service.DefaultAccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private final AccountNumberGenerator accountNumberGenerator = new DefaultAccountNumberGenerator();

    @GetMapping("/add-account")
    public String showAddAccountPage() {
        return "add-account";
    }

    @PostMapping("/add-account")
    public String addAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                             @RequestParam String currency,
                             @RequestParam double balance) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        User user = customUserDetails.getUser();

        Account account = new Account();
        account.setUserId(user.getId());
        account.setBalance(balance);
        account.setCurrency(currency);
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        // Генерация номера через стратегию
        account.setAccountNumber(accountNumberGenerator.generate());

        accountRepository.save(account);

        return "redirect:/my-accounts";
    }



    // Страница "Мои счета"
    @GetMapping("/my-accounts")
    public String showMyAccounts(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        User user = customUserDetails.getUser();
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        model.addAttribute("accounts", accounts);
        return "my-accounts";
    }


    @GetMapping("/account/edit/{id}")
    public String editAccount(@PathVariable Long id, Model model) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Счет не найден"));

        if (!account.isActive()) {
            model.addAttribute("errorMessage", "Этот счет неактивен и не может быть изменен.");
            return "account-error"; // Перенаправляем на отдельную страницу с ошибкой
        }

        model.addAttribute("account", account);
        return "edit-account"; // Показываем форму изменения
    }


    @PostMapping("/account/update")
    public String updateAccount(@ModelAttribute("account") Account account, Model model) {
        Account existingAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Счет не найден"));



        // Обновляем только допустимые поля
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setBalance(account.getBalance());

        // Обновляем дату обновления
        existingAccount.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(existingAccount);

        return "redirect:/my-accounts";
    }


}
