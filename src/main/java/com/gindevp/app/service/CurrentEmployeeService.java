package com.gindevp.app.service;

import com.gindevp.app.domain.User;
import com.gindevp.app.repository.UserRepository;
import com.gindevp.app.security.AuthoritiesConstants;
import com.gindevp.app.security.SecurityUtils;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Nhân viên liên kết với user đăng nhập ({@code jhi_user.employee_id}).
 */
@Service
public class CurrentEmployeeService {

    private final UserRepository userRepository;

    public CurrentEmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Long> currentEmployeeId() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .map(User::getEmployee)
            .filter(Objects::nonNull)
            .map(e -> e.getId());
    }

    public boolean isAssetManagerOrAdmin() {
        return SecurityUtils.hasCurrentUserAnyOfAuthorities(
            AuthoritiesConstants.ADMIN,
            AuthoritiesConstants.ASSET_MANAGER,
            AuthoritiesConstants.GD
        );
    }
}
