package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitorService {

    private final VisitorMapper visitorMapper;
    private final ParkUserRepository parkUserRepository;

    public void registerVisitor (VisitorRegisteredEvent event) {
        Visitor visitor = visitorMapper.toEntity(event);

        visitor.setUserType(ParkUserType.VISITOR);
        parkUserRepository.save(visitor);

        event.futureResponse().complete(new RegisterAccountResponseDTO(event.email()));
    }
}
