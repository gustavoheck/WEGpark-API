package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.infra.repository.OperationRepository;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OperationServiceTest {
    private RhRepository rhRepository;
    private OperationRepository operationRepository;
    private OperationService service;

    @BeforeEach
    void setUp() {
        rhRepository = mock(RhRepository.class);
        operationRepository = mock(OperationRepository.class);
        service = new OperationService(rhRepository, operationRepository);
    }

    @Test
    void persistsOperationForRhUserAndSkipsItForAdmin() {
        UUID uuid = UUID.randomUUID();
        Rh rh = new Rh(1L, uuid, "rh@weg.net", "1", "RH", "1");
        when(rhRepository.findByUuid(uuid)).thenReturn(Optional.of(rh));
        service.saveOperation(new JWTUserData(uuid, "rh@weg.net", List.of(RolesType.ROLE_RH.name()), "RH"), 2L, OperationType.UPDATE);
        verify(operationRepository).save(argThat(operation -> operation.getRh() == rh && operation.getIdOperatedUser().equals(2L)));

        service.saveOperation(new JWTUserData(UUID.randomUUID(), "admin@weg.net", List.of(RolesType.ROLE_ADMIN.name()), "Admin"), 2L, OperationType.UPDATE);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void rejectsOperationWhenRhIsUnknown() {
        UUID uuid = UUID.randomUUID();
        when(rhRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.saveOperation(new JWTUserData(uuid, "rh@weg.net", List.of(RolesType.ROLE_RH.name()), "RH"), 2L, OperationType.CREATE));
    }
}
