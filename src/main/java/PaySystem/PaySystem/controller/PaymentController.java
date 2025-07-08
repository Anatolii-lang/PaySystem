package PaySystem.PaySystem.controller;

import PaySystem.PaySystem.model.Account;
import PaySystem.PaySystem.model.Bill;
import PaySystem.PaySystem.model.User;
import PaySystem.PaySystem.repository.AccountRepository;
import PaySystem.PaySystem.repository.BillRepository;
import PaySystem.PaySystem.service.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment") // Убедитесь, что все пути начинаются с /payment
public class PaymentController {

    private final AccountRepository accountRepository;
    private final BillRepository billRepository; // Зависимость для работы с счетами

    // ===== ПЕРЕВОД СРЕДСТВ =====

    // Показать страницу перевода
    @GetMapping("/transfer-money")
    public String showTransferPage() {
        return "payment/transfer-money";
    }

    // Обработка перевода
    @PostMapping("/transfer-money")
    @Transactional
    public String transferMoney(@RequestParam Long fromAccountNumber,
                                @RequestParam Long toAccountNumber,
                                @RequestParam double amount,
                                Model model) {

        Optional<Account> fromAccountOpt = accountRepository.findByAccountNumber(fromAccountNumber);
        Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(toAccountNumber);

        if (fromAccountOpt.isEmpty() || toAccountOpt.isEmpty()) {
            model.addAttribute("error", "Один из счетов не найден.");
            return "payment/transfer-money";
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        // Проверка активности счетов
        if (!fromAccount.isActive() || !toAccount.isActive()) {
            model.addAttribute("error", "Оба счета должны быть активными для перевода.");
            return "payment/transfer-money";
        }

        // Проверка валюты
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            model.addAttribute("error", "Валюты счетов должны совпадать.");
            return "payment/transfer-money";
        }

        // Проверка баланса
        if (fromAccount.getBalance() < amount) {
            model.addAttribute("error", "Недостаточно средств для перевода.");
            return "payment/transfer-money";
        }

        // Перевод средств
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        fromAccount.setUpdatedAt(java.time.LocalDateTime.now());
        toAccount.setUpdatedAt(java.time.LocalDateTime.now());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        model.addAttribute("success", "Перевод выполнен успешно!");

        return "payment/transfer-money";
    }

    // ===== ПОПОЛНЕНИЕ СЧЕТА =====

    // Показать страницу пополнения
    @GetMapping("/recharge-account")
    public String showRechargePage() {
        return "payment/recharge-account";
    }

    // Обработка пополнения
    @PostMapping("/recharge-account")
    @Transactional
    public String rechargeAccount(@RequestParam Long accountId,
                                  @RequestParam double amount,
                                  Model model) {

        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountId);

        if (accountOpt.isEmpty()) {
            model.addAttribute("error", "Счет не найден.");
            return "payment/recharge-account";
        }

        Account account = accountOpt.get();

        // Проверка активности счета
        if (!account.isActive()) {
            model.addAttribute("error", "Невозможно пополнить неактивный счет.");
            return "payment/recharge-account";
        }

        // Пополнение счета
        account.setBalance(account.getBalance() + amount);
        account.setUpdatedAt(java.time.LocalDateTime.now());
        accountRepository.save(account);

        model.addAttribute("success", "Пополнение прошло успешно!");

        return "payment/recharge-account";
    }

    // ===== ОПЛАТА УСЛУГ =====

    // Страница всех счетов пользователя
    @GetMapping("/my-bills")
    public String showUserBills(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        if (customUserDetails == null) {
            return "redirect:/login"; // Перенаправление на страницу входа
        }

        User user = customUserDetails.getUser();
        List<Bill> bills = billRepository.findByUser(user); // Получаем все счета этого пользователя

        model.addAttribute("bills", bills);
        return "payment/my-bills"; // Страница для отображения счетов
    }

    // Страница для оплаты счета
    @GetMapping("/pay-bill/{billId}")
    public String showPayBillPage(@PathVariable Long billId, @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        Bill bill = billRepository.findById(billId).orElse(null);

        if (bill != null && bill.getUser().getId().equals(customUserDetails.getUser().getId())) {
            List<Account> accounts = accountRepository.findByUserId(customUserDetails.getUser().getId());
            model.addAttribute("bill", bill);
            model.addAttribute("accounts", accounts);
            return "payment/pay-bill";
        } else {
            return "redirect:/payment/my-bills";
        }
    }



    // Обработка оплаты счета
    @PostMapping("/pay-bill/{billId}")
    @Transactional
    public String payBill(@PathVariable Long billId,
                          @RequestParam Long accountId,
                          @AuthenticationPrincipal CustomUserDetails customUserDetails,
                          Model model) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        Bill bill = billRepository.findById(billId).orElse(null);

        if (bill != null && bill.getUser().getId().equals(customUserDetails.getUser().getId())) {
            Account account = accountRepository.findById(accountId).orElse(null);

            if (account == null || account.getBalance() < bill.getAmount()) {
                model.addAttribute("error", "Недостаточно средств на счете.");
                // Вот здесь!
                model.addAttribute("bill", bill);
                model.addAttribute("accounts", accountRepository.findByUserId(customUserDetails.getUser().getId()));
                return "payment/pay-bill";
            }

            account.setBalance(account.getBalance() - bill.getAmount());
            accountRepository.save(account);

            bill.setPaid(true);
            billRepository.save(bill);

            model.addAttribute("success", "Счет успешно оплачен!");
            model.addAttribute("bill", bill);
            model.addAttribute("accounts", accountRepository.findByUserId(customUserDetails.getUser().getId()));
            return "payment/pay-bill";
        } else {
            model.addAttribute("error", "Счет не найден.");
            return "payment/pay-bill";
        }
    }





}
