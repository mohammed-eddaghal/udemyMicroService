package com.medd.accountservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.medd.accountservice.dto.AccountResponseDto;
import com.medd.accountservice.dto.AccountsDTO;
import com.medd.accountservice.entities.Accounts;

@Mapper(componentModel = "spring")
public interface AccountsMapper {

    /**
     * Map JPA entity {@link Accounts} to {@link AccountsDTO}.
     * Field names match so MapStruct will map them automatically.
     */
    AccountsDTO toAccountsDTO(Accounts accounts);

    /**
     * Map {@link AccountsDTO} to JPA entity {@link Accounts}.
     * Ignore customer field as it's handled separately in the service.
     */
    @Mapping(target = "customer", ignore = true)
    Accounts toAccounts(AccountsDTO accountsDTO);

    /**
     * Map {@link AccountsDTO} to {@link AccountResponseDto}.
     * Account number is masked to show only last 4 digits.
     */
    @Mapping(target = "accountNumber", source = "accountNumber", qualifiedByName = "maskAccountNumber")
    AccountResponseDto toAccountResponse(AccountsDTO accountsDTO);

    /**
     * Mask account number to show only last 4 digits.
     * Example: 1234567890123456 becomes **** **** **** 3456
     */
    @Named("maskAccountNumber")
    default String maskAccountNumber(Long accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        String accountStr = String.valueOf(accountNumber);
        if (accountStr.length() < 4) {
            return accountStr;
        }
        String lastFour = accountStr.substring(accountStr.length() - 4);
        return "**** **** **** " + lastFour;
    }

}

