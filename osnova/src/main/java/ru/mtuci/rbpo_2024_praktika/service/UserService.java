package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo_2024_praktika.dto.RegistrationUserDto;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.repository.RoleRepository;
import ru.mtuci.rbpo_2024_praktika.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found",username)
        ));
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public Optional<ApplicationUser> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public ApplicationUser findById(UUID Id){
        return userRepository.findById(Id).orElseThrow(()->new UsernameNotFoundException("User with ID" + Id + "not found."));
    }

    public ApplicationUser createNewUser(RegistrationUserDto registrationUserDto){
        ApplicationUser user = new ApplicationUser();
        user.setUsername(registrationUserDto.username());
        user.setEmail(registrationUserDto.email());
        user.setPassword(passwordEncoder.encode(registrationUserDto.password()));
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return userRepository.save(user);
    }

}
