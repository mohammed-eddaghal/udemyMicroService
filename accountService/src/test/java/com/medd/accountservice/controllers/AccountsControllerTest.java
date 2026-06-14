package com.medd.accountservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medd.accountservice.constants.AccountTypes;
import com.medd.accountservice.dto.AccountResponseDto;
import com.medd.accountservice.dto.AccountsDTO;
import com.medd.accountservice.dto.AccountsUpdateDTO;
import com.medd.accountservice.dto.CustomerDTO;
import com.medd.accountservice.exception.AccountAlreadyExistsException;
import com.medd.accountservice.exception.ResourceNotExistsException;
import com.medd.accountservice.services.AccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountsService accountsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO customerDTO;
    private AccountResponseDto accountResponseDto;
    private final String BASE_URL = "/api/v1/accounts";

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO("John Doe", "john@example.com", "1234567890");
        accountResponseDto = new AccountResponseDto("XXXX-XXXX-1234", AccountTypes.VISA, "Main Street", customerDTO);
    }

    @Test
    @DisplayName("POST /api/v1/accounts - Success")
    void saveNewAccount_Success() throws Exception {
        AccountsDTO accountsDTO = new AccountsDTO(1234567890L, AccountTypes.VISA, "Main Street", customerDTO);
        when(accountsService.saveNewAccount(any(AccountsDTO.class))).thenReturn(accountResponseDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountsDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber").value("XXXX-XXXX-1234"))
                .andExpect(jsonPath("$.accountType").value("VISA"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"));
    }

    @Test
    @DisplayName("POST /api/v1/accounts - Account Already Exists")
    void saveNewAccount_AlreadyExists() throws Exception {
        AccountsDTO accountsDTO = new AccountsDTO(1234567890L, AccountTypes.VISA, "Main Street", customerDTO);
        when(accountsService.saveNewAccount(any(AccountsDTO.class)))
                .thenThrow(new AccountAlreadyExistsException("Account already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountsDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Account already exists"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts - Success")
    void getAccountDetails_Success() throws Exception {
        Long accountNumber = 1234567890L;
        when(accountsService.getAccountDetails(accountNumber)).thenReturn(accountResponseDto);

        mockMvc.perform(get(BASE_URL)
                        .param("accountNumber", accountNumber.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber").value("XXXX-XXXX-1234"))
                .andExpect(jsonPath("$.customer.email").value("john@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts - Not Found")
    void getAccountDetails_NotFound() throws Exception {
        Long accountNumber = 1234567890L;
        when(accountsService.getAccountDetails(accountNumber))
                .thenThrow(new ResourceNotExistsException("Account not found"));

        mockMvc.perform(get(BASE_URL)
                        .param("accountNumber", accountNumber.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not exist"));
    }

    @Test
    @DisplayName("PUT /api/v1/accounts - Success")
    void updateAccount_Success() throws Exception {
        Long accountNumber = 1234567890L;
        AccountsUpdateDTO updateDTO = new AccountsUpdateDTO(AccountTypes.MASTERCARD, "New Street", customerDTO);
        
        AccountResponseDto updatedResponse = new AccountResponseDto("XXXX-XXXX-1234", AccountTypes.MASTERCARD, "New Street", customerDTO);

        when(accountsService.updateAccount(any(AccountsUpdateDTO.class), eq(accountNumber))).thenReturn(updatedResponse);

        mockMvc.perform(put(BASE_URL)
                        .param("accountNumber", accountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("MASTERCARD"))
                .andExpect(jsonPath("$.branchAddress").value("New Street"));
    }

    @Test
    @DisplayName("PUT /api/v1/accounts - Not Found")
    void updateAccount_NotFound() throws Exception {
        Long accountNumber = 1234567890L;
        AccountsUpdateDTO updateDTO = new AccountsUpdateDTO(AccountTypes.MASTERCARD, "New Street", customerDTO);

        when(accountsService.updateAccount(any(AccountsUpdateDTO.class), eq(accountNumber)))
                .thenThrow(new ResourceNotExistsException("Account not found"));

        mockMvc.perform(put(BASE_URL)
                        .param("accountNumber", accountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not exist"));
    }
}
