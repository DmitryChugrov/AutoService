package com.InfoSystem.AutoService.Controller;

import com.InfoSystem.AutoService.ForAuto.Brand;
import com.InfoSystem.AutoService.ForOrder.OrderStatus;
import com.InfoSystem.AutoService.ForUsers.UserRole;
import com.InfoSystem.AutoService.Model.Auto;
import com.InfoSystem.AutoService.Model.Order;
import com.InfoSystem.AutoService.Model.ScheduleSlot;
import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Repository.AutoRepository;
import com.InfoSystem.AutoService.Repository.OrderRepository;
import com.InfoSystem.AutoService.Repository.ScheduleSlotRepository;
import com.InfoSystem.AutoService.Service.AutoService;
import com.InfoSystem.AutoService.Service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

// Класс - контроллер для пользователя с ролью "Клиент"
@Controller
public class ClientController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private AutoService autoService;
    @Autowired
    private ScheduleSlotRepository scheduleSlotRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AutoRepository autoRepository;
    private OrderStatus orderStatus;
    private UserRole userRole;

//   Метод для показа главной страницы
    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        String email = principal.getName();

        Users user = usersService.findByEmail(email);

        model.addAttribute("user", user);

        if (user != null) {
            if (userRole.OPERATOR.getRoleName().equals(user.getRole())) {
                model.addAttribute("isOperator", true);
            } else if (userRole.MANAGER.getRoleName().equals(user.getRole())) {
                model.addAttribute("isManager", true);
            } else if (userRole.CLIENT.getRoleName().equals(user.getRole())) {
                model.addAttribute("isClient", true);
            }
        } else {
            model.addAttribute("error", "Пользователь не найден.");
        }

        return "home";
    }

//  Метод для показа страницы "Добавить автомобиля"
    @GetMapping("/addAuto")
    public String showAddAutoForm(Model model) {
        model.addAttribute("auto", new Auto());
        List<Brand> brands = Arrays.asList(Brand.values());
        model.addAttribute("brands", brands);
        return "addAuto";
    }
//  Вспомогательный метод для вывода моделей автомобилей
    @ResponseBody
    @GetMapping("/models")
    public Brand.Model[] getModelsByBrand(@RequestParam String brand) {
        return Brand.valueOf(brand).getModels();
    }
    //  Вспомогательный метод для вывода моделей автомобилей
    @ResponseBody
    @GetMapping("/generations")
    public String[] getGenerationsByModel(@RequestParam String brand, @RequestParam String model) {
        return Arrays.stream(Brand.valueOf(brand).getModels())
                .filter(m -> m.getName().equals(model))
                .findFirst()
                .map(Brand.Model::getGenerations)
                .orElse(new String[]{});
    }
    // Метод для принятия введенных данных пользователя об автомобиле
    @PostMapping("/addAuto")
    public String addAuto(@Valid @ModelAttribute("auto") Auto auto,
                          BindingResult result, Model model,
                          @AuthenticationPrincipal UserDetails userDetails, Principal principal) {

        if (result.hasErrors()) {
            return "addAuto";
        }

        String vinNumberPattern = "^[0-9A-HJ-NPR-Z]{17}$";
        if (!auto.getVinNumber().matches(vinNumberPattern)) {
            result.rejectValue("vinNumber", "error.auto", "VIN должен содержать только допустимые символы и быть длиной 17 символов");
        }

        String gosNumberPattern = "^[АВЕКМНОРСТУХ]{1}[0-9]{3}[АВЕКМНОРСТУХ]{2}[0-9]{2,3}$";
        if (!auto.getGOS_number().matches(gosNumberPattern)) {
            result.rejectValue("GOS_number", "error.auto", "Госномер должен быть в формате: одна буква, три цифры, две буквы, и номер региона (2 или 3 цифры)");
        }

        if (result.hasErrors()) {
            return "addAuto";
        }

        if (autoService.isVinExists(auto.getVinNumber())) {
            model.addAttribute("vinError", "Автомобиль с таким VIN уже существует.");
            return "addAuto";
        }

        Users currentUser = usersService.findByEmail(principal.getName());
        if (currentUser != null) {
            auto.setUser(currentUser);
            autoService.saveAuto(auto);
            model.addAttribute("message", "Автомобиль успешно добавлен!");
        } else {
            model.addAttribute("error", "Пользователь не найден.");
            return "addAuto";
        }

        return "redirect:/home";
    }

//    Метод вывода страницы "Мои автомобили"
    @GetMapping("/autos")
    public String listUserAutos(Model model, Principal principal) {
        Users currentUser = usersService.findByEmail(principal.getName());

        if (currentUser != null) {
            List<Auto> userAutos = autoService.findByUser(currentUser);
            model.addAttribute("autos", userAutos);
        } else {
            model.addAttribute("error", "Пользователь не найден.");
        }

        return "userAutos";
    }
    // Методы для записи пользователя на услуги в автосервис
    @GetMapping("/selectAutoAndService")
    public String selectAutoAndService(Model model, Principal principal) {
        Users currentUser = usersService.findByEmail(principal.getName());
        if (currentUser != null) {
            List<Auto> userAutos = autoService.findByUser(currentUser);
            model.addAttribute("userAutos", userAutos);
        }
        return "selectAutoAndService";
    }

    @PostMapping("/selectAutoAndService")
    public String submitAutoAndServiceSelection(@RequestParam("carId") Long carId,
                                                @RequestParam("serviceType") String serviceType,
                                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("carId", carId);
        redirectAttributes.addAttribute("serviceType", serviceType);
        return "redirect:/selectSlot";
    }
    @GetMapping("/selectSlot")
    public String showAvailableSlots(@RequestParam("carId") Long carId,
                                     @RequestParam("serviceType") String serviceType,
                                     Model model) {
        LocalDate today = LocalDate.now();
        List<ScheduleSlot> availableSlots = scheduleSlotRepository.findByAvailableTrue()
                .stream()
                .filter(slot -> !slot.getDate().isBefore(today))
                .toList();

        model.addAttribute("slots", availableSlots);
        model.addAttribute("carId", carId);
        model.addAttribute("serviceType", serviceType);
        return "selectSlot";
    }

    @GetMapping("/confirmOrder")
    public String confirmOrder(@RequestParam("slotId") Long slotId,
                               @RequestParam("carId") Long carId,
                               @RequestParam("serviceType") String serviceType,
                               Model model,
                               Principal principal) {

        Users currentUser = usersService.findByEmail(principal.getName());

        if (currentUser != null) {
            List<Auto> userAutos = autoService.findByUser(currentUser);
            model.addAttribute("userAutos", userAutos);
        }
        ScheduleSlot slot = scheduleSlotRepository.findById(slotId).orElseThrow();
        LocalDateTime dateTime = LocalDateTime.of(slot.getDate(), slot.getTime());

        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        model.addAttribute("formattedDateTime", formattedDateTime);

        model.addAttribute("slot", slot);
        model.addAttribute("serviceType", serviceType);

        Auto auto = autoRepository.findById(carId).orElseThrow();
        model.addAttribute("auto", auto);

        return "confirmOrder";
    }

    @PostMapping("/confirmOrder")
    public String bookOrder(@RequestParam("slotId") Long slotId,
                            @RequestParam("carId") Long carId,
                            @RequestParam("serviceType") String serviceType,
                            @RequestParam(value = "comment", required = false) String comment,
                            Principal principal) {

        ScheduleSlot slot = scheduleSlotRepository.findById(slotId).orElseThrow();
        slot.setAvailable(false);

        Order order = new Order();
        order.setSlot(slot);

        Users currentUser = usersService.findByEmail(principal.getName());
        if (currentUser != null) {
            order.setClient(currentUser);
        } else {
            throw new IllegalArgumentException("Текущий пользователь не найден");
        }

        Auto auto = autoRepository.findById(carId).orElseThrow();
        if (auto.getUser().equals(currentUser)) {
            order.setAuto(auto);
        } else {
            throw new IllegalArgumentException("Выбранный автомобиль не принадлежит текущему пользователю");
        }

        order.setServiceType(serviceType);
        order.setClientComment(comment);
        order.setStatus(orderStatus.AWAITING_CONFIRMATION.getValue());

        orderRepository.save(order);
        scheduleSlotRepository.save(slot);

        return "okOrder";
    }
//  Метод для показа страницы "История обращений"
    @GetMapping("/orders")
    public String getClientOrders(Model model, Principal principal) {
        Users currentUser = usersService.findByEmail(principal.getName());
        if (currentUser != null) {
            List<Order> orders = orderRepository.findByClient(currentUser);
            model.addAttribute("orders", orders);
        }
        return "clientOrders";
    }




}