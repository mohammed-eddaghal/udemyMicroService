package com.medd.accountservice.services.impl;

import java.util.Random;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medd.accountservice.dto.AccountResponseDto;
import com.medd.accountservice.dto.AccountsDTO;
import com.medd.accountservice.entities.Accounts;
import com.medd.accountservice.entities.Customer;
import com.medd.accountservice.mappers.AccountsMapper;
import com.medd.accountservice.mappers.CustomerMapper;
import com.medd.accountservice.repositories.AccountsRepository;
import com.medd.accountservice.repositories.CustomerRepository;
import com.medd.accountservice.services.AccountsService;
import com.medd.accountservice.exception.AccountAlreadyExistsException;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements AccountsService {


    private final AccountsRepository accountsRepository;

    private final CustomerRepository customerRepository;

    private final AccountsMapper accountsMapper;

    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public AccountResponseDto saveNewAccount(AccountsDTO accountsDTO) {

        // Check if account already exists (by accountNumber) and fail fast
        if (accountsRepository.existsByAccountNumber(accountsDTO.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Account with number " + accountsMapper.maskAccountNumber(accountsDTO.getAccountNumber()) + " already exists");
        }

        // Save customer if provided
        Customer customer = null;
        if (accountsDTO.getCustomer() != null) {
            // Check if a customer with same name and mobile number already exists
            var customerDto = accountsDTO.getCustomer();
            var existing = customerRepository.findByNameAndMobileNumber(customerDto.getName(), customerDto.getMobileNumber());
            if (existing.isPresent()) {
                customer = existing.get();
            } else {
                customer = customerMapper.toCustomer(customerDto);
                customer = customerRepository.save(customer);
            }
        }

        // Generate unique 16-digit account number (like a credit card)
        Long uniqueAccountNumber = generateUniqueAccountNumber();

        Accounts account = new Accounts();
        account.setAccountNumber(uniqueAccountNumber);
        account.setAccountType(accountsDTO.getAccountType());
        account.setBranchAddress(accountsDTO.getBranchAddress());
        account.setCustomer(customer);


        // Save account
        Accounts savedAccount = accountsRepository.save(account);

        // Convert to AccountsDTO first, then map to AccountResponseDto with masked account number
        AccountsDTO accountsDTO_result = accountsMapper.toAccountsDTO(savedAccount);
        return accountsMapper.toAccountResponse(accountsDTO_result);
    }

    /**
     * Generate a unique 16-digit account number (like a credit card ID).
     * Uses a combination of timestamp and random numbers to ensure uniqueness.
     *
     * @return a unique 16-digit Long account number
     */
    private Long generateUniqueAccountNumber() {
        // Combine timestamp (milliseconds) with random numbers
        // Timestamp gives us about 13 digits, we add 3 random digits for a total of 16
        long timestamp = System.currentTimeMillis();

        // Get last 13 digits of timestamp
        long timestampPart = timestamp % 10_000_000_000_000L;

        // Generate 3 random digits (0-999)
        Random random = new Random();
        int randomPart = random.nextInt(1000);

        // Combine: timestampPart (13 digits) + randomPart (3 digits) = 16 digits
        return timestampPart * 1000 + randomPart;
    }

}

