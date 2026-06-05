package com.medd.accountservice.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Provides the current auditor (user) for JPA auditing.
 * Returns "SYSTEM" as the default auditor.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Return "SYSTEM" as the default auditor
        // In a real application, this would return the current authenticated user
        return Optional.of("SYSTEM");
    }

}

