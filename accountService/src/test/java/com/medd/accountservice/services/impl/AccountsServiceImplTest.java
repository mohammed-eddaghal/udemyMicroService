package com.medd.accountservice.services.impl;

import com.medd.accountservice.constants.AccountTypes;
import com.medd.accountservice.dto.AccountResponseDto;
import com.medd.accountservice.dto.AccountsDTO;
import com.medd.accountservice.dto.AccountsUpdateDTO;
import com.medd.accountservice.dto.CustomerDTO;
import com.medd.accountservice.entities.Accounts;
import com.medd.accountservice.entities.Customer;
import com.medd.accountservice.exception.AccountAlreadyExistsException;
import com.medd.accountservice.exception.ResourceNotExistsException;
import com.medd.accountservice.mappers.AccountsMapper;
import com.medd.accountservice.mappers.CustomerMapper;
import com.medd.accountservice.repositories.AccountsRepository;
import com.medd.accountservice.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsServiceImplTest {

    private static final Long ACCOUNT_NUMBER = 1234567890123456L;
    private static final String MASKED_ACCOUNT_NUMBER = "XXXX-XXXX-XXXX-3456";

    @Mock
    private AccountsRepository accountsRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountsMapper accountsMapper;
    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private AccountsServiceImpl accountsService;

    private Accounts account;
    private AccountsDTO accountsDTO;
    private AccountResponseDto responseDto;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        account = new Accounts();
        accountsDTO = new AccountsDTO();
        accountsDTO.setAccountNumber(ACCOUNT_NUMBER);
        responseDto = new AccountResponseDto();
    }

    @Test
    @DisplayName("saveNewAccount should throw exception when account already exists")
    void saveNewAccount_AccountExists_ThrowsException() {
        // Given
        when(accountsRepository.existsByAccountNumber(anyLong())).thenReturn(true);
        when(accountsMapper.maskAccountNumber(anyLong())).thenReturn(MASKED_ACCOUNT_NUMBER);

        // When & Then
        assertThatThrownBy(() -> accountsService.saveNewAccount(accountsDTO))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessageContaining("already exists");
        
        verify(accountsRepository).existsByAccountNumber(anyLong());
    }

    @Test
    @DisplayName("saveNewAccount should save new customer and account successfully")
    void saveNewAccount_NewCustomer_Success() {
        // Given
        CustomerDTO customerDTO = createCustomerDTO("John Doe", "1234567890");
        accountsDTO.setCustomer(customerDTO);
        accountsDTO.setAccountType(AccountTypes.VISA);

        when(accountsRepository.existsByAccountNumber(anyLong())).thenReturn(false);
        when(customerRepository.findByNameAndMobileNumber(any(), any())).thenReturn(Optional.empty());
        when(customerMapper.toCustomer(any(CustomerDTO.class))).thenReturn(new Customer());
        when(customerRepository.save(any(Customer.class))).thenReturn(new Customer());
        setupMapperMocks();

        // When
        AccountResponseDto result = accountsService.saveNewAccount(accountsDTO);

        // Then
        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
        verify(accountsRepository).save(any(Accounts.class));
    }

    @Test
    @DisplayName("saveNewAccount should use existing customer if found")
    void saveNewAccount_ExistingCustomer_Success() {
        // Given
        CustomerDTO customerDTO = createCustomerDTO("Jane Doe", "0987654321");
        accountsDTO.setCustomer(customerDTO);

        when(accountsRepository.existsByAccountNumber(anyLong())).thenReturn(false);
        when(customerRepository.findByNameAndMobileNumber("Jane Doe", "0987654321")).thenReturn(Optional.of(new Customer()));
        setupMapperMocks();

        // When
        AccountResponseDto result = accountsService.saveNewAccount(accountsDTO);

        // Then
        assertThat(result).isNotNull();
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("saveNewAccount should succeed even if no customer is provided")
    void saveNewAccount_NoCustomer_Success() {
        // Given
        accountsDTO.setCustomer(null);

        when(accountsRepository.existsByAccountNumber(anyLong())).thenReturn(false);
        setupMapperMocks();

        // When
        AccountResponseDto result = accountsService.saveNewAccount(accountsDTO);

        // Then
        assertThat(result).isNotNull();
        verify(customerRepository, never()).findByNameAndMobileNumber(any(), any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("getAccountDetails should return details when account exists")
    void getAccountDetails_AccountExists_ReturnsDetails() {
        // Given
        when(accountsRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(accountsMapper.toAccountsDTO(account)).thenReturn(accountsDTO);
        when(accountsMapper.toAccountResponse(accountsDTO)).thenReturn(responseDto);

        // When
        AccountResponseDto result = accountsService.getAccountDetails(ACCOUNT_NUMBER);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getAccountDetails should throw exception when account not found")
    void getAccountDetails_AccountNotFound_ThrowsException() {
        // Given
        when(accountsRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());
        when(accountsMapper.maskAccountNumber(ACCOUNT_NUMBER)).thenReturn(MASKED_ACCOUNT_NUMBER);

        // When & Then
        assertThatThrownBy(() -> accountsService.getAccountDetails(ACCOUNT_NUMBER))
                .isInstanceOf(ResourceNotExistsException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("updateAccount should update and return new details")
    void updateAccount_AccountExists_Success() {
        // Given
        AccountsUpdateDTO updateDTO = new AccountsUpdateDTO();
        when(accountsRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(accountsMapper.toAccountsDto(any(AccountsUpdateDTO.class))).thenReturn(accountsDTO);
        when(accountsMapper.toAccounts(any(AccountsDTO.class))).thenReturn(account);
        setupMapperMocks();

        // When
        AccountResponseDto result = accountsService.updateAccount(updateDTO, ACCOUNT_NUMBER);

        // Then
        assertThat(result).isNotNull();
        verify(accountsRepository).save(any(Accounts.class));
    }

    @Test
    @DisplayName("updateAccount should throw exception when account not found")
    void updateAccount_AccountNotFound_ThrowsException() {
        // Given
        AccountsUpdateDTO updateDTO = new AccountsUpdateDTO();
        when(accountsRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());
        when(accountsMapper.maskAccountNumber(ACCOUNT_NUMBER)).thenReturn(MASKED_ACCOUNT_NUMBER);

        // When & Then
        assertThatThrownBy(() -> accountsService.updateAccount(updateDTO, ACCOUNT_NUMBER))
                .isInstanceOf(ResourceNotExistsException.class)
                .hasMessageContaining("not found");
    }

    private CustomerDTO createCustomerDTO(String name, String mobile) {
        CustomerDTO dto = new CustomerDTO();
        dto.setName(name);
        dto.setMobileNumber(mobile);
        return dto;
    }

    private void setupMapperMocks() {
        when(accountsRepository.save(any(Accounts.class))).thenReturn(account);
        when(accountsMapper.toAccountsDTO(any(Accounts.class))).thenReturn(accountsDTO);
        when(accountsMapper.toAccountResponse(any(AccountsDTO.class))).thenReturn(responseDto);
    }
}
