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
public class AccountsDTO {

    private Long accountNumber;
    private AccountTypes accountType;
    private String branchAddress;
    private CustomerDTO customer;
}

