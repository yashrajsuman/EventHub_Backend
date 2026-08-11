package com.eventhub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:eventhub-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired private MockMvc mvc;
    private static final String EVENT_JSON = """
            {"title":"Festival setup crew","description":"A community gig.","venue":"Town Hall","startsAt":"2030-10-20T18:00:00","capacity":25,"numberOfDays":2,"dailyPay":1200}
            """;

    @Test
    void creatingAnEventRequiresAdminAuthentication() throws Exception {
        mvc.perform(post("/api/admin/gigs").contentType(MediaType.APPLICATION_JSON).content(EVENT_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void administratorCanCreateAnEvent() throws Exception {
        mvc.perform(post("/api/admin/gigs").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(EVENT_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void administratorCanSignInAndCorsAllowsTheAngularApp() throws Exception {
        mvc.perform(get("/api/admin/session").with(httpBasic("admin", "admin123"))
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void workerCanConfirmRegistrationWithAnUploadedPicture() throws Exception {
        String event = mvc.perform(post("/api/admin/gigs").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(EVENT_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String eventId = event.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");
        String picture = "data:image/png;base64," + "A".repeat(1_000);
        String registration = """
                {"name":"Sam Worker","email":"sam@example.com","phoneNumber":"+91 98765 43210","age":25,
                 "gender":"Non-binary","location":"Pune","height":170,"weight":65,"education":"Bachelor's",
                 "experience":"Event setup","picture":"%s"}
                """.formatted(picture);

        mvc.perform(post("/api/gigs/{eventId}/registrations", eventId)
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registeredCount").value(1))
                .andExpect(jsonPath("$.spotsLeft").value(24));
    }
}
