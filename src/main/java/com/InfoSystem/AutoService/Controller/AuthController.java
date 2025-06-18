package com.InfoSystem.AutoService.Controller;

import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Service.UsersService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//  Класс - контроллер для авторизации и регистрации пользователя
@Controller
public class AuthController {
    private final UsersService usersService;

    public AuthController(UsersService userService) {
        this.usersService = userService;
    }
//    Метод для авторизации
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
//    Методы для регистрации пользователя
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("Users", new Users());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("Users") Users users,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        if (!usersService.registerUser(users)) {
            model.addAttribute("error", "Пользователь с таким email уже существует");
            return "register";
        }
        return "confirmation";
    }
}
