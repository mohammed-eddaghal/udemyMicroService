package com.medd.accountservice.repositories;

import com.medd.accountservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	/**
	 * Find a customer by name and mobile number. Used to avoid creating duplicate customers.
	 */
	Optional<Customer> findByNameAndMobileNumber(String name, String mobileNumber);


}

