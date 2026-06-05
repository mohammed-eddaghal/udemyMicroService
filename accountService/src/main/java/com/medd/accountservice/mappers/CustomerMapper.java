package com.medd.accountservice.mappers;

import org.mapstruct.Mapper;

import com.medd.accountservice.dto.CustomerDTO;
import com.medd.accountservice.entities.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    /**
     * Map {@link CustomerDTO} to JPA entity {@link Customer}.
     */
    Customer toCustomer(CustomerDTO customerDTO);

    /**
     * Map JPA entity {@link Customer} to {@link CustomerDTO}.
     */
    CustomerDTO toCustomerDTO(Customer customer);

}

