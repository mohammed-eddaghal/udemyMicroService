package com.medd.accountservice.services;

import com.medd.accountservice.dto.AccountResponseDto;
import com.medd.accountservice.dto.AccountsDTO;

public interface AccountsService {

    /**
     * Create and save a new account with a unique 16-digit account number.
     * The account type is set to VISA and branch address to Rabat by default.
     * Customer information is saved first if provided.
     *
     * @param accountsDTO the account data with embedded customer info
     * @return account response with masked account number (last 4 digits only)
     */
    AccountResponseDto saveNewAccount(AccountsDTO accountsDTO);

}

