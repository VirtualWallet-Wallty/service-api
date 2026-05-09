package com.krushkov.virtualwallet.helpers;

public final class ConstantMessages {

    private ConstantMessages() {}

    // Swagger constants
    public final static String CONTACT_NAME = "Todor Krushkov";
    public final static String CONTACT_URL = "https://github.com/todorkrushkov";
    public final static String CONTACT_EMAIL = "todorkrushkov.1304@gmail.com";

    public final static String OPEN_API_TITLE = "Wallty Service API";
    public final static String OPEN_API_DESCRIPTION =
            "Secure REST API for virtual transactions, payments & finance management in different currencies.";
    public final static String OPEN_API_VERSION = "1.0.0";

    // Swagger controller constants
    public final static String AUTH_CONTROLLER_NAME = "Authentication";
    public final static String AUTH_CONTROLLER_DESCRIPTION = "Authentication and session management";
    public final static String AUTH_CONTROLLER_REGISTER = "Register new user";
    public final static String AUTH_CONTROLLER_LOGIN = "Login existing user";
    public final static String AUTH_CONTROLLER_LOGOUT = "Logout current user";
    public final static String AUTH_CONTROLLER_GET_ME = "Get current user's info";

    public final static String CARD_CONTROLLER_NAME = "Card";
    public final static String CARD_CONTROLLER_DESCRIPTION = "Cards management";
    public final static String CARD_CONTROLLER_GET_ALL_BY_USER_ID = "[ADMIN ONLY] Get all cards by user ID";
    public final static String CARD_CONTROLLER_GET_MY_ALL = "[USER ONLY] Get current user cards";
    public final static String CARD_CONTROLLER_GET_BY_ID = "Get card by ID";
    public final static String CARD_CONTROLLER_ADD = "[USER ONLY] Add a new card as a current user";
    public final static String CARD_CONTROLLER_REMOVE = "Remove card";
    public final static String CARD_CONTROLLER_ACTIVATE = "Set card status to active";
    public final static String CARD_CONTROLLER_DEACTIVATE = "Set card status to deactivated";

    public final static String CURRENCY_CONTROLLER_NAME = "Currency";
    public final static String CURRENCY_CONTROLLER_DESCRIPTION = "Currency management";
    public final static String CURRENCY_CONTROLLER_GET_ALL_ACTIVE = "Get all currencies";
    public final static String CURRENCY_CONTROLLER_GET_BY_CODE = "Get currency by its ISO 4217 code";
    public final static String CURRENCY_CONTROLLER_CREATE = "[ADMIN ONLY] Create new currency";
    public final static String CURRENCY_CONTROLLER_ACTIVATE = "[ADMIN ONLY] Activate a currency by its ISO 4217 code";
    public final static String CURRENCY_CONTROLLER_DEACTIVATE = "[ADMIN ONLY] Deactivate a currency by its ISO 4217 code";

    public final static String EXCHANGE_RATE_CONTROLLER_NAME = "Exchange Rate";
    public final static String EXCHANGE_RATE_CONTROLLER_DESCRIPTION = "Exchange rates management";
    public final static String EXCHANGE_RATE_CONTROLLER_GET_ALL = "Get all exchange rates";
    public final static String EXCHANGE_RATE_CONTROLLER_GET_ALL_BY_CURRENCY = "Get all exchange rates for currency";
    public final static String EXCHANGE_RATE_CONTROLLER_GET_RATE = "Get exchange rate between 2 currencies";
    public final static String EXCHANGE_RATE_CONTROLLER_SYNC = "[ADMIN ONLY] Sync all exchange rates for currency";

    public final static String PAYMENT_CONTROLLER_NAME = "Payment";
    public final static String PAYMENT_CONTROLLER_DESCRIPTION = "Process payment";
    public final static String PAYMENT_CONTROLLER_PAY = "[USER ONLY] Process payment";

    public final static String TOP_UP_CONTROLLER_NAME = "Top-Up";
    public final static String TOP_UP_CONTROLLER_DESCRIPTION = "Process top-up";
    public final static String TOP_UP_CONTROLLER_TOP_UP = "[USER ONLY] Process top-up";

    public final static String TRANSACTION_CONTROLLER_NAME = "Transaction";
    public final static String TRANSACTION_CONTROLLER_DESCRIPTION = "Transactions management";
    public final static String TRANSACTION_CONTROLLER_SEARCH = "Search all transactions";
    public final static String TRANSACTION_CONTROLLER_GET_BY_ID = "Get transaction by ID";

    public final static String TRANSFER_CONTROLLER_NAME = "Transfer";
    public final static String TRANSFER_CONTROLLER_DESCRIPTION = "Process transfer";
    public final static String TRANSFER_CONTROLLER_TRANSFER = "[USER ONLY] Process transfer";

    public final static String USER_CONTROLLER_NAME = "User";
    public final static String USER_CONTROLLER_DESCRIPTION = "Users management";
    public final static String USER_CONTROLLER_SEARCH = "Search all users";
    public final static String USER_CONTROLLER_GET_BY_ID = "Get user by ID";
    public final static String USER_CONTROLLER_UPDATE = "Update current user";
    public final static String USER_CONTROLLER_BLOCK = "Block user by its ID";
    public final static String USER_CONTROLLER_UNBLOCK = "Unblock user by its ID";

    public final static String WALLET_CONTROLLER_NAME = "Wallet";
    public final static String WALLET_CONTROLLER_DESCRIPTION = "Wallets management";
    public final static String WALLET_CONTROLLER_SEARCH = "[ADMIN ONLY] Search wallets";
    public final static String WALLET_CONTROLLER_GET_ALL_BY_USER_ID = "[ADMIN ONLY] Get all wallets by user ID";
    public final static String WALLET_CONTROLLER_GET_MY_ALL = "[USER ONLY] Get current user's all wallets";
    public final static String WALLET_CONTROLLER_GET_BY_ID = "Get wallet by ID";
    public final static String WALLET_CONTROLLER_GET_MY_DEFAULT = "[USER ONLY] Get current user's default wallet";
    public final static String WALLET_CONTROLLER_CREATE = "[USER ONLY] Create new wallet";
    public final static String WALLET_CONTROLLER_UPDATE = "[USER ONLY] Update current user's wallet";
    public final static String WALLET_CONTROLLER_DELETE = "[USER ONLY] Delete current user's wallet";
    public final static String WALLET_CONTROLLER_SET_DEFAULT = "[USER ONLY] Set current user's default wallet";

    // ApiResponse messages
    public final static String REGISTER_SUCCESS_MESSAGE = "Register successfully.";
    public final static String LOGIN_SUCCESS_MESSAGE = "Login successfully.";
    public final static String LOGOUT_SUCCESS_MESSAGE = "Logout successfully.";

    public final static String CARD_ADDED_SUCCESSFULLY_MESSAGE = "Card added successfully.";
    public final static String CARD_REMOVED_SUCCESSFULLY_MESSAGE = "Card removed successfully.";
    public final static String CARD_ACTIVATED_SUCCESSFULLY_MESSAGE = "Card activated successfully.";
    public final static String CARD_DEACTIVATED_SUCCESSFULLY_MESSAGE = "Card activated successfully.";

    public final static String CURRENCY_CREATE_SUCCESS_MESSAGE = "Currency created successfully.";
    public final static String CURRENCY_ACTIVATE_SUCCESS_MESSAGE = "Currency activated successfully.";
    public final static String CURRENCY_DEACTIVATE_SUCCESS_MESSAGE = "Currency deactivated successfully.";

    public final static String EXCHANGE_RATE_SYNC_SUCCESS_MESSAGE = "Exchange rates synced successfully.";

    public final static String PAYMENT_SUCCESS_MESSAGE = "Payment successful.";

    public final static String TOP_UP_SUCCESS_MESSAGE = "Top-Up successful.";

    public final static String TRANSFER_SUCCESS_MESSAGE = "Transfer successful.";

    public final static String USER_UPDATE_SUCCESS_MESSAGE = "User updated successfully.";
    public final static String USER_BLOCK_SUCCESS_MESSAGE = "User blocked successfully.";
    public final static String USER_UNBLOCK_SUCCESS_MESSAGE = "User unblocked successfully.";

    public final static String WALLET_CREATE_SUCCESS_MESSAGE = "Wallet created successfully.";
    public final static String WALLET_UPDATE_SUCCESS_MESSAGE = "Wallet updated successfully.";
    public final static String WALLET_DELETE_SUCCESS_MESSAGE = "Wallet deleted successfully.";
    public final static String WALLET_SET_DEFAULT_SUCCESS_MESSAGE = "Wallet set default successfully.";

    // ApiResponse HTTP status descriptions
    public static final String HTTP_OK = "OK";
    public static final String HTTP_BAD_REQUEST = "Bad request";
    public static final String HTTP_NOT_FOUND = "Not found";
    public static final String HTTP_CONFLICT = "Conflict";

    // Auth request errors
    public final static String IDENTIFIER_PASSWORD_MISSING_ERROR = "Username/Email and password are required.";
    public final static String IDENTIFIER_PASSWORD_WRONG_ERROR = "Username/email or password are wrong.";

    // User request errors
    public final static String USERNAME_LENGTH_ERROR = "Username must be between {min} and {max} symbols.";
    public final static String PASSWORD_LENGTH_ERROR = "Password must be between {min} and {max} symbols.";
    public final static String FIRST_NAME_LENGTH_ERROR = "First name must be between {min} and {max} symbols.";
    public final static String LAST_NAME_LENGTH_ERROR = "Last name must be between {min} and {max} symbols.";
    public final static String EMAIL_LENGTH_ERROR = "Email must be between {min} and {max} symbols.";
    public final static String PHONE_NUMBER_LENGTH_ERROR = "Phone number must be {max} symbols.";
    public final static String PHOTO_URL_LENGTH_ERROR = "Photo URL must be between {min} and {max} symbols.";
    public final static String USER_CREATE_RANGE_ERROR = "Created from must be before or equal to created to.";

    public final static String USERNAME_NOT_NULL_ERROR = "Username is required.";
    public final static String FIRST_NAME_NOT_NULL_ERROR = "First name is required.";
    public final static String LAST_NAME_NOT_NULL_ERROR = "Last name is required.";
    public final static String EMAIL_NOT_NULL_ERROR = "Email is required.";
    public final static String PASSWORD_NOT_NULL_ERROR = "Password is required.";

    public final static String EMAIL_INVALID_ERROR = "Email is invalid.";

    // Card request errors
    public final static String CARD_HOLDER_LENGTH_ERROR = "Card holder must be between {min} and {max} characters.";
    public final static String CARD_NUMBER_LENGTH_ERROR = "Card number must be 16 digits.";

    public final static String CARD_HOLDER_NOT_NULL_ERROR = "Card holder is required.";
    public final static String CARD_NUMBER_NOT_NULL_ERROR = "Card number is required.";
    public final static String CARD_EXPIRATION_MONTH_NOT_NULL_ERROR = "Expiration month is required.";
    public final static String CARD_EXPIRATION_YEAR_NOT_NULL_ERROR = "Expiration year is required.";

    public final static String CARD_EXPIRED_ERROR = "Card is expired.";

    // Currency request errors
    public final static String CURRENCY_CODE_NOT_NULL_ERROR = "Currency code is required.";
    public final static String CURRENCY_NAME_NOT_NULL_ERROR = "Currency name is required.";
    public final static String CURRENCY_SYMBOL_NOT_NULL_ERROR = "Currency symbol is required.";
    public final static String CURRENCY_DECIMALS_NOT_NULL_ERROR = "Currency decimals is required.";

    public final static String CURRENCY_CODE_LENGTH_ERROR = "Currency code must be {max} symbols (ISO 4217).";
    public final static String CURRENCY_NAME_LENGTH_ERROR = "Currency name cannot be greater that {max} symbols.";
    public final static String CURRENCY_SYMBOL_LENGTH_ERROR =
            "Currency symbol cannot be greater than {max} characters.";
    public final static String CURRENCY_MIN_DECIMALS_LENGTH_ERROR = "Currency decimals must be positive.";
    public final static String CURRENCY_MAX_DECIMALS_LENGTH_ERROR =
            "Currency decimals cannot be grater than 10 digits.";

    // Transaction request errors
    public final static String TRANSACTION_CREATE_RANGE_ERROR = "Created from must be before or equal to created to.";
    public final static String TRANSACTION_AMOUNT_RANGE_ERROR = "Min amount must be less than or equal to max amount.";

    // Wallet request errors
    public final static String WALLET_NAME_NOT_NULL_ERROR = "Password is required.";
    public final static String WALLET_NAME_LENGTH_ERROR = "Wallet name must be between {min} and {max} symbols.";
    public final static String WALLET_USER_ID_LENGTH_ERROR = "User Id must be positive.";
    public final static String WALLET_MIN_BALANCE_LENGTH_ERROR = "Minimal balance must be positive or zero.";
    public final static String WALLET_MAX_BALANCE_LENGTH_ERROR = "Maximal balance must be positive or zero.";
    public final static String WALLET_BALANCE_RANGE_ERROR = "Min balance must be less than or equal to max balance.";

    // Payment/Top-Up/Transfer request errors
    public final static String AMOUNT_LENGTH_ERROR = "Amount must be greater that zero.";
    public final static String AMOUNT_NOT_NULL_ERROR = "Amount must be not null.";
    public final static String WALLET_ID_LENGTH_ERROR = "Wallet ID must be positive.";
    public final static String WALLET_ID_NOT_NULL_ERROR = "Wallet ID is required.";
    public final static String CARD_ID_LENGTH_ERROR = "Card ID must be positive.";
    public final static String CARD_ID_NOT_NULL_ERROR = "Card ID is required.";
    public final static String RECIPIENT_ID_NOT_NULL_ERROR = "Recipient ID must be not null.";

    // User validation errors
    public static final String ADMIN_ONLY_ERROR = "Only admins are allowed to perform this action.";
    public static final String USER_ONLY_ERROR = "Only users are allowed to perform this action.";
    public static final String USER_BLOCKED_ERROR = "Blocked users cannot perform this action.";
    public static final String RECIPIENT_BLOCKED_ERROR = "User is blocked, you cannot perform this action.";
    public static final String RECIPIENT_NOT_ADMIN_ERROR = "You cannot perform this action.";

    // Transaction validation errors
    public static final String WRONG_CURRENCY_ERROR = "Currency mismatch.";
    public static final String POSITIVE_AMOUNT_ERROR = "Amount must be positive.";
    public static final String INSUFFICIENT_FUNDS_ERROR = "Insufficient funds.";

    // Card validation errors
    public static final String CARD_ACTIVATE_ERROR = "Card is not active.";
    public static final String CARD_ALREADY_ACTIVE_ERROR = "Card is already active.";
    public static final String CARD_ALREADY_DEACTIVATE_ERROR = "Card is already deactivated.";
    public static final String CARD_DEACTIVATED_BY_ADMIN_ERROR = "Card was already deactivated by administrator.";
    public static final String CARD_ALREADY_EXISTS_ERROR = "User already have this card.";

    // Wallet validation errors
    public static final String WALLET_NAME_ALREADY_EXISTS_ERROR = "User already have wallet with the same name.";
    public static final String WALLET_ALREADY_DEFAULT_ERROR = "This wallet is already default.";
    public static final String WALLET_BALANCE_NOT_EMPTY_ERROR = "Wallet is not empty.";
    public static final String WALLET_MAX_COUNT_ERROR = "You cannot have more than %d wallets.";
    public static final String WALLET_MIN_COUNT_ERROR = "You cannot have less than %d wallet.";

    // Api access errors
    public static final String API_ACCESS_ERROR = "You are not allowed to access this resource.";
    public static final String AUTHENTICATION_MISSING_ERROR = "Authentication required.";
    public static final String INTERNAL_SERVER_ERROR = "Unexpected server error.";
    public static final String WRONG_ENDPOINT_ERROR = "Endpoint not found.";
    public static final String WRONG_CREDENTIALS_ERROR = "Wrong username or password.";
    public static final String VALIDATION_ERROR = "Validation failed.";

    // JWT errors
    public static final String INVALID_TOKEN_ERROR = "Invalid JWT token: ";

    // User service errors
    public static final String USER_ALREADY_BLOCKED = "User %s is already blocked.";
    public static final String USER_NOT_BLOCKED = "User %s is not blocked.";

}
