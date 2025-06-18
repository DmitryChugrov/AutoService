package com.InfoSystem.AutoService.Controller;

import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

// Вспомогательный класс - контроллер для изменения данных пользователя
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService userService;

//    Методы для редактирования данных клиента
    @GetMapping("/edit")
    public String editUserForm(Principal principal, Model model) {
        Users user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "edit_user";
    }
    @PostMapping("/edit")
    public String updateUser(
            @ModelAttribute("user") @Valid Users user,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit_user";
        }
        userService.updateUser(principal.getName(), user);
        redirectAttributes.addFlashAttribute("successMessage", "Данные успешно обновлены!");
        return "redirect:/home";
    }
}
