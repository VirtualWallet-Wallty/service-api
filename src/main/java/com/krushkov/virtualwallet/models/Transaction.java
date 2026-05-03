package com.krushkov.virtualwallet.models;

import jakarta.persistence.*;
import com.krushkov.virtualwallet.models.enums.TransactionStatus;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "transaction_id")
    private Long id;

    @Column(name = "label", length = 50)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "sender_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal senderAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_currency_code", nullable = false)
    private Currency senderCurrency;

    @Column(name = "recipient_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal recipientAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_currency_code", nullable = false)
    private Currency recipientCurrency;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 8)
    private BigDecimal exchangeRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_wallet_id")
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_wallet_id")
    private Wallet recipientWallet;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
