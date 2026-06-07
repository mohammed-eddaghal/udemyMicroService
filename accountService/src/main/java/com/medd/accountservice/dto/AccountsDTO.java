package com.medd.accountservice.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.medd.accountservice.constants.AccountTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountsDTO {

    @NotEmpty(message = "Account number must not be empty")
    private Long accountNumber;
    @NotEmpty(message = "Account type must not be empty")
    private AccountTypes accountType;
    private String branchAddress;
    private CustomerDTO customer;
}

