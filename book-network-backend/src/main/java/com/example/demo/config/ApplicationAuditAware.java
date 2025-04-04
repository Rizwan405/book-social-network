package com.example.demo.config;

import com.example.demo.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<Integer> {
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken || authentication == null){
            return Optional.empty();
        }
        User userprincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userprincipal.getId());
    }
}
