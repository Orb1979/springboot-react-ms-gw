package com.example.msf.customer.web;

import com.example.msf.customer.model.Customer;
import com.example.msf.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private Customer existingCustomer;
    private Customer requestBody;

    @BeforeEach
    void setUp() {
        existingCustomer = Customer.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@example.com")
                .phoneNumber("555-0100")
                .build();

        requestBody = Customer.builder()
                .name("John Smith")
                .email("john@example.com")
                .phoneNumber("555-0200")
                .build();
    }

    @Test
    void findAll_returnsCustomers() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(existingCustomer));

        mockMvc.perform(get("/api/customer/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$[0].email").value("jane@example.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("555-0100"));
    }

    @Test
    void findOne_returnsCustomer_whenFound() throws Exception {
        when(customerService.findOne(1L)).thenReturn(Optional.of(existingCustomer));

        mockMvc.perform(get("/api/customer/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("555-0100"));
    }

    @Test
    void findOne_returnsNotFound_whenMissing() throws Exception {
        when(customerService.findOne(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customer/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returnsCreatedCustomer() throws Exception {
        Customer saved = Customer.builder()
                .id(2L)
                .name(requestBody.getName())
                .email(requestBody.getEmail())
                .phoneNumber(requestBody.getPhoneNumber())
                .build();
        when(customerService.create(any(Customer.class))).thenReturn(saved);

        mockMvc.perform(post("/api/customer/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/customer/2"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("555-0200"));

        verify(customerService).create(any(Customer.class));
    }

    @Test
    void update_returnsCustomer_whenFound() throws Exception {
        Customer updated = Customer.builder()
                .id(1L)
                .name(requestBody.getName())
                .email(requestBody.getEmail())
                .phoneNumber(requestBody.getPhoneNumber())
                .build();
        when(customerService.update(eq(1L), any(Customer.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/customer/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("555-0200"));
    }

    @Test
    void update_returnsNotFound_whenMissing() throws Exception {
        when(customerService.update(eq(99L), any(Customer.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/customer/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNoContent_whenDeleted() throws Exception {
        when(customerService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/customer/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).delete(1L);
    }

    @Test
    void delete_returnsNotFound_whenMissing() throws Exception {
        when(customerService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/customer/customers/99"))
                .andExpect(status().isNotFound());
    }
}
