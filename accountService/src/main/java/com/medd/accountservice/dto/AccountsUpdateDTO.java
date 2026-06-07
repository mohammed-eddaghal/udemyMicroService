package com.medd.accountservice.dto;

import com.medd.accountservice.constants.AccountTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountsUpdateDTO {

    private AccountTypes accountType;
    private String branchAddress;
    private CustomerDTO customer;
}

