package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorResponseDTO;
import com.weg.WEGpark.rh.UpdateVisitorEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitorService {

    private final VisitorMapper visitorMapper;
    private final VisitorRepository visitorRepository;
    private final ParkUserRepository parkUserRepository;

    public void registerVisitor (VisitorRegisteredEvent event) {
        Visitor visitor = visitorMapper.toEntity(event);

        visitor.setUserType(ParkUserType.VISITOR);
        parkUserRepository.save(visitor);

        event.futureResponse().complete(new RegisterAccountResponseDTO(visitor.getUuid(), event.email()));
    }

    @Transactional
    public UpdateVisitorResponseDTO updateVisitorRequest (UpdateVisitorRequestDTO request, JWTUserData jwtUserData) {
        if (jwtUserData.roles().contains(RolesType.ROLE_RH.name())) {
            throw new AccessDeniedException("Rh users can not update park user registration data");
        }

        Visitor visitor = visitorRepository.findByUuid(jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any visitor was found by %s uuid".formatted(jwtUserData.uuid())));

        visitorMapper.updateFromDTO(request, visitor);

        visitorRepository.save(visitor);

        return visitorMapper.toUpdateResponse(visitor);
    }

    public void updateVisitorEvent (UpdateVisitorEvent event) {
        Optional<Visitor> optVisitor = visitorRepository.findByUuid(event.parkUserUuid());

        if (optVisitor.isPresent()) {
            Visitor visitor = optVisitor.get();

            visitorMapper.updateFromEvent(event, visitor);

            visitorRepository.save(visitor);

            event.eventResponse().complete(visitorMapper.toUpdateResponseFromEvent(event));
        }
        event.eventResponse().completeExceptionally(new NotFoundException("Any collaborator was found by %s uuid".formatted(event.parkUserUuid())));
    }
}
