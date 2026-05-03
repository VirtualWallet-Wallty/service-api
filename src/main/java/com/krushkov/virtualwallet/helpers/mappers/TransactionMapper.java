package com.krushkov.virtualwallet.helpers.mappers;

import com.krushkov.virtualwallet.models.Transaction;
import com.krushkov.virtualwallet.models.dtos.responses.transaction.TransactionLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.transaction.TransactionShortResponse;
import com.krushkov.virtualwallet.models.enums.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, WalletMapper.class, CurrencyMapper.class}
)
public interface TransactionMapper {

    @Mapping(target = "senderCurrencyCode", source = "transaction.senderCurrency.code")
    @Mapping(target = "recipientCurrencyCode", source = "transaction.recipientCurrency.code")
    @Mapping(target = "senderWalletId", source = "transaction.senderWallet.id")
    @Mapping(target = "recipientWalletId", source = "transaction.recipientWallet.id")
    @Mapping(target = "direction", expression = "java(resolveDirection(transaction, currentUserId))")
    TransactionShortResponse toShort(Transaction transaction, Long currentUserId);

    @Mapping(target = "direction", expression = "java(resolveDirection(transaction, currentUserId))")
    TransactionLongResponse toLong(Transaction transaction, Long currentUserId);

    default String resolveDirection(Transaction tx, Long currentUserId) {

        Long senderId = tx.getSenderWallet() != null && tx.getSenderWallet().getUser() != null
                ? tx.getSenderWallet().getUser().getId()
                : null;

        Long recipientId = tx.getRecipientWallet() != null && tx.getRecipientWallet().getUser() != null
                ? tx.getRecipientWallet().getUser().getId()
                : null;

        boolean isSender = currentUserId.equals(senderId);
        boolean isRecipient = currentUserId.equals(recipientId);

        if (isSender && isRecipient) {
            return "INTERNAL";
        }

        if (isSender) {
            return "OUTGOING";
        }

        if (isRecipient) {
            return "INCOMING";
        }

        return "UNKNOWN";
    }
}
