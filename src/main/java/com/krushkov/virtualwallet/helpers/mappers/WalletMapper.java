package com.krushkov.virtualwallet.helpers.mappers;

import com.krushkov.virtualwallet.models.User;
import com.krushkov.virtualwallet.models.Wallet;
import com.krushkov.virtualwallet.models.dtos.requests.user.UserUpdateRequest;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletCreateRequest;
import com.krushkov.virtualwallet.models.dtos.requests.wallet.WalletUpdateRequest;
import com.krushkov.virtualwallet.models.dtos.responses.wallet.WalletLongResponse;
import com.krushkov.virtualwallet.models.dtos.responses.wallet.WalletShortResponse;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {CurrencyMapper.class}
)
public interface WalletMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "default", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Wallet fromCreate(WalletCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "default", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(@MappingTarget Wallet wallet, WalletUpdateRequest request);

    @Mapping(target = "ownerId", source = "user.id")
    @Mapping(target = "currencyCode", source = "currency.code")
    @Mapping(target = "isDefault", source = "default")
    WalletShortResponse toShort(Wallet wallet);

    @Mapping(target = "isDefault", source = "default")
    @Mapping(target = "owner", source = "user")
    WalletLongResponse toLong(Wallet wallet);
}
