package com.InfoSystem.AutoService.Controller;


import com.InfoSystem.AutoService.ForAuto.Brand;
import com.InfoSystem.AutoService.ForStats.OrderStatistics;
import com.InfoSystem.AutoService.ForUsers.UserRole;
import com.InfoSystem.AutoService.Model.Auto;
import com.InfoSystem.AutoService.Model.Order;
import com.InfoSystem.AutoService.Model.ScheduleSlot;
import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Repository.OrderRepository;
import com.InfoSystem.AutoService.Repository.ScheduleSlotRepository;
import com.InfoSystem.AutoService.Service.AutoService;
import com.InfoSystem.AutoService.Service.OrderService;
import com.InfoSystem.AutoService.Service.StatisticsService;
import com.InfoSystem.AutoService.Service.UsersService;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.InfoSystem.AutoService.ForAuto.Brand.parseBrand;

// Класс - контроллер для пользователя с ролью "Менеджер"
@Controller
public class ManagerController {
    @Autowired
    private ScheduleSlotRepository scheduleSlotRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    private AutoService autoService;
    @Autowired
    private OrderService orderService;
    private final OrderRepository orderRepository;
    private final StatisticsService statisticsService;
    private UserRole userRole;

    public ManagerController(OrderRepository orderRepository, StatisticsService statisticsService) {
        this.orderRepository = orderRepository;
        this.statisticsService = statisticsService;
    }

//    Метод вывода страницы "Список всех автомобилей"
    @GetMapping("/manager/allAutos")
    public String viewAllAutos(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "vinNumber", required = false) String vinNumber,
            @RequestParam(value = "gosNumber", required = false) String gosNumber,
            @RequestParam(value = "email", required = false) String email,
            Model modelAttribute) {

        List<Auto> autos;

        if (brand == null && model == null && vinNumber == null && gosNumber == null && email == null) {
            autos = autoService.getAllAutos();
        } else {
            autos = autoService.searchAutos(brand, model, vinNumber, gosNumber, email);
        }

        modelAttribute.addAttribute("autos", autos);
        return "allAutos";
    }
    // Методы для добавления менеджером слотов для записи на услуги в автосервис
    @GetMapping("/manager/addScheduleSlot")
    public String showAddSlotPage(Model model) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByAvailableTrue();
        model.addAttribute("slots", slots);
        return "addScheduleSlot";
    }
    @PostMapping("/manager/addScheduleSlot")
    public String addScheduleSlot(@RequestParam("date") String date,
                                  @RequestParam("time") String time) {
        ScheduleSlot slot = new ScheduleSlot();
        slot.setDate(LocalDate.parse(date));
        slot.setTime(LocalTime.parse(time));
        slot.setAvailable(true);
        scheduleSlotRepository.save(slot);
        return "redirect:/manager/viewSlots";
    }
    @PostMapping("/manager/addFullDaySlots")
    public ResponseEntity<String> addFullDaySlots(@RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<ScheduleSlot> slots = new ArrayList<>();
            System.out.println("Добавление слотов для даты: " + date);

            for (int hour = 8; hour <= 19; hour++) {
                LocalTime time = LocalTime.of(hour, 0);
                boolean slotExists = scheduleSlotRepository.existsByDateAndTime(date, time);

                if (!slotExists) {
                    ScheduleSlot slot = new ScheduleSlot();
                    slot.setDate(date);
                    slot.setTime(time);
                    slot.setAvailable(true);
                    slots.add(slot);
                    System.out.println("Добавлен слот для времени: " + time);
                } else {
                    System.out.println("Слот для времени " + time + " уже существует, пропускаем.");
                }
            }

            if (!slots.isEmpty()) {
                scheduleSlotRepository.saveAll(slots);
                System.out.println("Все слоты на день сохранены успешно.");
                return ResponseEntity.ok("Слоты на весь день добавлены успешно.");
            } else {
                return ResponseEntity.ok("Слоты уже существуют на весь день.");
            }

        } catch (DateTimeParseException e) {
            System.err.println("Ошибка формата даты: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный формат даты. Ожидаемый формат: yyyy-MM-dd.");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении слотов: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении слотов: " + e.getMessage());
        }
    }
//  Метод для показа страницы "Доступные слоты для записи"
    @GetMapping("/manager/viewSlots")
    public String viewAvailableSlots(Model model) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByAvailableTrue();
        model.addAttribute("slots", slots);
        return "viewSlots";
    }

//    Метод вывода страницы "Список клиентов"
    @GetMapping("/manager/clients")
    public String viewClients(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Users> clients;

        if (search != null && !search.isEmpty()) {
            clients = usersService.searchClients(search);
        } else {
            clients = usersService.findByRole(userRole.CLIENT.getRoleName()); 
        }

        model.addAttribute("clients", clients);
        model.addAttribute("searchQuery", search); 
        return "managerClients"; 
    }
    @PostMapping("/manager/orders/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam int status) {
        Order order = orderService.findById(orderId);
        order.setStatus(status);
        orderService.save(order);
        return "redirect:/manager/orders";
    }
    @GetMapping("/manager/orders/addComment")
    public String showAddCommentPage(@RequestParam Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "addComment";
    }
    @PostMapping("/manager/orders/addComment")
    public String saveManagerComment(@RequestParam Long orderId, @RequestParam String managerComment) {
        Order order = orderService.findById(orderId);
        order.setManagerComment(managerComment);
        orderService.save(order);
        return "redirect:/manager/orders";
    }

//    Метод вывода страницы "Заказы"
    @GetMapping("/manager/orders")
    public String searchOrders(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String vinNumber,
            @RequestParam(required = false) String GOS_number,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String clientEmail,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String serviceType,
            Model modelAttribute) {

        Brand parsedBrand = parseBrand(brand);

        LocalDate parsedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        System.out.println(parsedDate);
        List<Order> orders = orderRepository.findOrdersByFilter(
                orderId, parsedBrand, model, year, vinNumber, GOS_number, parsedDate, clientEmail, status, serviceType);

        modelAttribute.addAttribute("orders", orders);
        return "managerOrders";
    }
//    Метод логики формирования заказ-наряда
    @PostMapping("/manager/orders/exportDocx")
    public ResponseEntity<?> generateOrderDocx(@RequestParam Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try (XWPFDocument document = new XWPFDocument()) {
                XWPFParagraph title = document.createParagraph();
                title.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = title.createRun();
                titleRun.setText("Заказ-наряд");
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                XWPFTable table = document.createTable();

                XWPFTableRow headerRow = table.getRow(0);
                headerRow.getCell(0).setText("Клиент");
                headerRow.addNewTableCell().setText("Автомобиль");
                headerRow.addNewTableCell().setText("VIN");
                headerRow.addNewTableCell().setText("Услуга");
                headerRow.addNewTableCell().setText("Дата и время");

                XWPFTableRow valueRow = table.createRow();
                valueRow.getCell(0).setText(order.getClient().getEmail());
                valueRow.getCell(1).setText(order.getAuto().getBrand() + " " + order.getAuto().getModel());
                valueRow.getCell(2).setText(order.getAuto().getVinNumber());
                valueRow.getCell(3).setText(order.getServiceType());
                valueRow.getCell(4).setText(order.getSlot().getDate() + " " + order.getSlot().getTime());

                XWPFParagraph commentsParagraph = document.createParagraph();
                XWPFRun commentsRun = commentsParagraph.createRun();
                commentsRun.addBreak();
                commentsRun.setText("Комментарий клиента: " + (order.getClientComment() != null ? order.getClientComment() : "Нет"));
                commentsRun.addBreak();
                commentsRun.setText("Комментарий менеджера: " + (order.getManagerComment() != null ? order.getManagerComment() : "Нет"));
                commentsRun.addBreak();
                commentsRun.setText("Цена: " + (order.getPrice() != null ? order.getPrice() : "0") + " руб." );
                    document.write(out);
                }


            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=order_" + orderId + ".docx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    private void addTableRow(XWPFTable table, String header, String value) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(header);
        row.addNewTableCell().setText(value);
    }
    @PostMapping("/manager/orders/setPrice")
    public String setOrderPrice(@RequestParam("orderId") Long orderId,
                                @RequestParam("price") Double price,
                                RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderPrice(orderId, price);
            redirectAttributes.addFlashAttribute("successMessage", "Цена успешно обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении цены: " + e.getMessage());
        }
        return "redirect:/manager/orders";
    }

//    Методы вывода статистики
    @GetMapping("/manager/statistics")
    public String showStatisticsForm() {
        return "managerStatsForm";
    }

    @GetMapping("/manager/statistics/results")
    public String getMonthlyStatistics(
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            Model model
    ) {
        Map<String, Object> statistics = statisticsService.calculateMonthlyStatistics(month, year);
        model.addAttribute("statistics", statistics);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        return "managerStatistics";
    }

}