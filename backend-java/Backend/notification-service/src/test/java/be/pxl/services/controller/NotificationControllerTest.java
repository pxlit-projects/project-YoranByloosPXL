package be.pxl.services.controller;

import be.pxl.services.TestBootApp;
import be.pxl.services.domain.Notification;
import be.pxl.services.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.HttpStatus.*;

@WebMvcTest(controllers = NotificationController.class)
@ContextConfiguration(classes = {TestBootApp.class, NotificationController.class})
class NotificationControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    NotificationService service;

    @Test
    void getForUser_returnsJsonList() throws Exception {
        Notification a = new Notification("ann", "hello");
        a.setCreatedAt(LocalDateTime.now());
        Notification b = new Notification("ann", "world");
        b.setCreatedAt(LocalDateTime.now().minusSeconds(1));

        when(service.getForUser("ann")).thenReturn(List.of(a, b));

        mvc.perform(get("/api/notifications").param("user", "ann"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].recipient").value("ann"))
                .andExpect(jsonPath("$[0].message").value("hello"))
                .andExpect(jsonPath("$[1].message").value("world"));

        verify(service).getForUser("ann");
    }

    @Test
    void deleteOne_returns204() throws Exception {
        mvc.perform(delete("/api/notifications/9").param("user", "ann"))
                .andExpect(status().isNoContent());
        verify(service).deleteOneForUser(9L, "ann");
    }

    @Test
    void deleteOne_whenMissing_returns404() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "Notification not found"))
                .when(service).deleteOneForUser(5L, "ann");

        mvc.perform(delete("/api/notifications/5").param("user", "ann"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAll_returns204() throws Exception {
        mvc.perform(delete("/api/notifications").param("user", "ann"))
                .andExpect(status().isNoContent());
        verify(service).deleteAllForUser("ann");
    }
}
