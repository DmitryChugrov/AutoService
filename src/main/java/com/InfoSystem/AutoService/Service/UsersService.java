package com.InfoSystem.AutoService.Service;

import com.InfoSystem.AutoService.Model.Users;
import com.InfoSystem.AutoService.Repository.UsersRepository;
import com.InfoSystem.AutoService.ForUsers.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

// Класс - сервис для реализации функций, связанных с обращениями к таблице с данными о пользователях
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String PHONE_PATTERN = "^\\+7 \\([0-9]{3}\\) [0-9]{3}-[0-9]{2}-[0-9]{2}$";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);
    private UserRole userRole;
    public UsersService(UsersRepository userRepository) {
        this.usersRepository = userRepository;

    }

    public boolean authenticate(String email, String password) {
        Users user = usersRepository.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }

        return false;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return pattern.matcher(phoneNumber).matches();
    }
    public boolean registerUser(Users newUser) {
        if (usersRepository.findByEmail(newUser.getEmail()) != null ) {
            return false;
        }
        if (!isValidPhoneNumber(newUser.getPhone_number())) {
            System.out.println(newUser.getPhone_number());
            return false;
        }
        newUser.setUsername(newUser.getUsername());
        newUser.setSurname(newUser.getSurname());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setPhone_number(newUser.getPhone_number());
        newUser.setEmail(newUser.getEmail());
        newUser.setRole(userRole.CLIENT.getRoleName());
        usersRepository.save(newUser);
        return true;
    }

    public boolean registerManager(Users newManager) {
        if (usersRepository.findByEmail(newManager.getEmail()) != null ) {
            return false;
        }
        if (!isValidPhoneNumber(newManager.getPhone_number())) {
            System.out.println(newManager.getPhone_number());
            return false;
        }
        newManager.setUsername(newManager.getUsername());
        newManager.setSurname(newManager.getSurname());
        newManager.setPassword(passwordEncoder.encode(newManager.getPassword()));
        newManager.setPhone_number(newManager.getPhone_number());
        newManager.setEmail(newManager.getEmail());
        newManager.setRole(userRole.MANAGER.getRoleName());
        usersRepository.save(newManager);
        return true;
    }
    public Users findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }
    public Users findByEmail(String email){
        return usersRepository.findByEmail(email);
    }
    public List<Users> searchClients(String query) {
        return usersRepository.findClientsBySearchQuery(query);
    }
    public List<Users> findByRole(String role) {
        return usersRepository.findByRole(role);
    }
    public List<Users> getAllPersonnel() {
        return usersRepository.findPersonnelByRoles(List.of(userRole.OPERATOR.getRoleName(), userRole.MANAGER.getRoleName()));
    }

    public List<Users> searchPersonnel(String query) {
        return usersRepository.searchPersonnelByQuery(query, List.of(userRole.OPERATOR.getRoleName(), userRole.MANAGER.getRoleName()));
    }
    public void updateUser(String email, Users updatedUser) {
        Users user = findByEmail(email);
        user.setUsername(updatedUser.getUsername());
        user.setSurname(updatedUser.getSurname());
        user.setEmail(updatedUser.getEmail());
        user.setPhone_number(updatedUser.getPhone_number());
        usersRepository.save(user);
    }
    public void deleteUserById(Long id) {
        usersRepository.deleteById(id);
    }
}
