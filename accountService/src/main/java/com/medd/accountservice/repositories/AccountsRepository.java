package com.medd.accountservice.repositories;

import com.medd.accountservice.entities.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {

	/**
	 * Returns true if an account with the given accountNumber exists.
	 */
	boolean existsByAccountNumber(Long accountNumber);

}

