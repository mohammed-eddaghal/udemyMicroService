package com.medd.accountservice.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.medd.accountservice.dto.AccountResponseDto;
import com.medd.accountservice.dto.AccountsDTO;
import com.medd.accountservice.services.AccountsService;

@RestController
@RequestMapping(value = "/api/v1/accounts", produces =  {MediaType.APPLICATION_JSON_VALUE})
public class AccountsController {

	@Autowired
	private AccountsService accountsService;

	/**
	 * Save a new account.
	 *
	 * @param accountsDTO the account data to save (with embedded customer info)
	 * @return ResponseEntity containing the saved account as response DTO with masked account number and HTTP 201 CREATED status
	 */
	@PostMapping
	public ResponseEntity<AccountResponseDto> saveNewAccount(@RequestBody AccountsDTO accountsDTO) {
		AccountResponseDto savedAccountResponse = accountsService.saveNewAccount(accountsDTO);
		return new ResponseEntity<>(savedAccountResponse, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<AccountResponseDto> getAccountDetails(@RequestParam Long accountNumber) {
		AccountResponseDto accountResponse = accountsService.getAccountDetails(accountNumber);
		return new ResponseEntity<>(accountResponse, HttpStatus.OK);
	}


}

