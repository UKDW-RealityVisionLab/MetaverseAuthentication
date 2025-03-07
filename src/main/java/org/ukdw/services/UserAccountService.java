package org.ukdw.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ukdw.dto.request.auth.SignUpRequest;
import org.ukdw.dto.user.UserRoleDTO;
import org.ukdw.entity.*;
import org.ukdw.exception.RequestParameterErrorException;
import org.ukdw.repository.GroupRepository;
import org.ukdw.repository.StudentRepository;
import org.ukdw.repository.TeacherRepository;
import org.ukdw.repository.UserAccountRepository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


//https://www.baeldung.com/spring-transactional-propagation-isolation
@Service
@RequiredArgsConstructor
public class UserAccountService {

    private static final Logger log = LogManager.getLogger(UserAccountService.class);
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public List<UserAccountEntity> listUserAccount() {
        return userAccountRepository.findAll();
    }

    @Transactional
    public UserAccountEntity createUserAccount(UserAccountEntity userAccount) {
        try{
            var newAccount = userAccountRepository.save(userAccount);
            return newAccount;
        }catch (DataIntegrityViolationException ex){
            throw new RequestParameterErrorException("A User with the same email or username already exists.");
        }
    }

    @Transactional
    public boolean deleteUserAccount(Long id){
        // Find the user
        Optional<UserAccountEntity> userOpt = userAccountRepository.findById(id);

        if(userOpt.isPresent()){
            UserAccountEntity user = userOpt.get();

            // Now delete the user
            userAccountRepository.delete(user);
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAccountEntity findUserAccountById(Long id) {
        Optional<UserAccountEntity> person = userAccountRepository.findById(id);
        return person.orElse(null);
    }

    @Transactional
    public boolean updateUserAccount(Long id, SignUpRequest updateRequest){
        Optional<UserAccountEntity> userOpt = userAccountRepository.findById(id);

        if (userOpt.isEmpty()) {
            return false;
        }

        UserAccountEntity user = userOpt.get();
        UserAccountEntity updatedUser = user;

        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(updateRequest.getPassword());
        }
        if (updateRequest.getUsername() != null) {
            user.setUsername(updateRequest.getUsername());
        }

        // Save the updated user entity
        userAccountRepository.save(updatedUser);
        return true;
    }

    public UserDetailsService userDetailsService() {
        return value -> {
            UserAccountEntity accountEntity;

            // Check if the value contains '@' to identify it as an email
            if (value.contains("@")) {
                accountEntity = userAccountRepository.findByEmail(value);
                if(accountEntity == null){
                    throw new UsernameNotFoundException("User not found with email: " + value);
                }
            } else {
                accountEntity = userAccountRepository.findByUsername(value)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + value));
            }

//            UserAccountEntity accountEntity = userAccountRepository.findByUsername(username)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new CustomUserDetails(accountEntity);
        };
    }
}
