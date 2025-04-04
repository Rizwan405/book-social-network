package com.example.demo.authentication;

import com.example.demo.email.EmailService;
import com.example.demo.role.RoleRepository;
import com.example.demo.security.JwtService;
import com.example.demo.token.TokenRepository;
import com.example.demo.user.RegistrationRequest;
import com.example.demo.user.User;
import com.example.demo.user.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.token.Token;
import com.example.demo.email.EmailTemplateName;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void  register(RegistrationRequest request) throws MessagingException {
//        search roles --done
//        build user object and save into user table
//        password encoder
//        save and send verification email

        var userRole =roleRepo.findByName("USER").orElseThrow(()->new IllegalArgumentException("Role not found"));
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enable(false)
                .roles(List.of(userRole))
                .build();
        userRepo.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        //    @Value("${application.mailing.frontend.activation-url}")
        String activationUrl = "http://localhost:4200/activate-account";
        emailService.sendEmail(user.getEmail(),user.fullName(),EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl,newToken,"Account activation");
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateToken(int length) {
        String characters = "01234567890";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0;i<length;i++){
            int randIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randIndex));

        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        var claims = new HashMap<String,Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName",user.fullName());
        var jwtToken = jwtService.generateToken(claims,user);

        return AuthenticationResponse.builder().token(jwtToken).build();

    }
//    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(()-> new RuntimeException("Invalid Token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has been expired. New token is send to email");
        }
        var user = userRepo.findById(savedToken.getUser().getId()).orElseThrow(()-> new RuntimeException("user not found"));
        user.setEnable(true);
        userRepo.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

    }
}
