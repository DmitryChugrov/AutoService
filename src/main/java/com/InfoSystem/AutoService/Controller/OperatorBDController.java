package com.InfoSystem.AutoService.Controller;

import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Service.UsersService;
import com.InfoSystem.AutoService.Service.DatabaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Класс - контроллер для пользователя с ролью "Администратор БД"
@Controller
@RequestMapping("/operatorBD")
public class OperatorBDController {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private UsersService usersService;
    // Метод для вывода списка существующих таблиц в БД
    @GetMapping("/tables")
    public String showTables(Model model) {
        List<String> tables = databaseService.getAllTables();
        model.addAttribute("tables", tables);
        return "operatorTablesList";
    }
    // Метод для вывода конкректной существующей таблицы в БД
    @GetMapping("/table/{tableName}")
    public String showTable(@PathVariable String tableName, Model model) {
        List<List<Object>> tableData = databaseService.getTableData(tableName);
        model.addAttribute("tableName", tableName);
        model.addAttribute("tableData", tableData);
        return "operatorTableData";
    }
    // Методы для регистрации менеджера в системе
    @GetMapping("/registerManagerForm")
    public String showRegisterManagerForm(Model model) {
        model.addAttribute("Manager", new Users());
        return "registerManager";
    }
    @PostMapping("/registerManager")
    public String registerManager(@Valid @ModelAttribute("Manager") Users manager,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "registerManager";
        }
        if (!usersService.registerManager(manager)){
            model.addAttribute("message", "Ошибка: менеджер с таким e-mail уже существует.");
        }
        return "confirmationManage";
    }

//    Метод вывода страницы "Список персонала"
    @GetMapping("/personnel")
    public String showPersonnel(
            @RequestParam(value = "search", required = false) String searchQuery,
            Model model) {
        List<Users> personnel;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            personnel = usersService.searchPersonnel(searchQuery);
        } else {
            personnel = usersService.getAllPersonnel();
        }
        model.addAttribute("clients", personnel);
        model.addAttribute("searchQuery", searchQuery);
        return "personnelList";
    }

//    Метод логики удаления персонала
    @PostMapping("/deleteClient")
    public String deleteClient(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usersService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении пользователя.");
        }
        return "redirect:/operatorBD/personnel";
    }
}