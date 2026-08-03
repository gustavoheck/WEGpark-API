package com.weg.WEGpark.integration;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.notification.internal.app.notification.service.EmailService;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VisitorRegistrationIntegrationTest extends AbstractPostgresIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private VisitorRepository visitorRepository;
    @MockitoBean private EmailService emailService;

    @Test
    void registersVisitorThroughTheAuthAndParkEventFlow() throws Exception {
        String email = "visitor-integration@weg.net";
        MvcResult result = mockMvc.perform(post("/auth/register/visitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "defaults":{"email":"visitor-integration@weg.net","password":"Valid@123"},
                                  "parkUserDefaults":{"name":"Integration Visitor","telephone":"11999999999"},
                                  "company":"WEG",
                                  "cpf":"12345678901"
                                }
                                """))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(email).stream().findFirst().orElseThrow();
        Visitor visitor = visitorRepository.findByUuid(user.getUuid()).orElseThrow();
        assertEquals("Integration Visitor", visitor.getName());
        assertEquals(email, visitor.getEmail());
        assertTrue(!user.getActive());
        assertTrue(!user.getEmailValidated());
    }
}
