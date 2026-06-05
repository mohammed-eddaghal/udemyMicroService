package com.medd.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.medd.accountservice.constants.AccountTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    private String accountNumber;
    private AccountTypes accountType;
    private String branchAddress;
    // Embedded customer DTO
    private CustomerDTO customer;

}

