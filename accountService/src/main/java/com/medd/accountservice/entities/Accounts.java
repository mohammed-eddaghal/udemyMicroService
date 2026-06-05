package com.medd.accountservice.entities;

import jakarta.persistence.*;
import com.medd.accountservice.constants.AccountTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accounts extends BaseEntity {

    @Id
    @Column(name = "account_number")
    private Long accountNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "account_type", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private AccountTypes accountType;

    @Column(name = "branch_address", nullable = false, length = 200)
    private String branchAddress;
}

