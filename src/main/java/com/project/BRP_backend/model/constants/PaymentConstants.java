package com.project.BRP_backend.model.constants;

public class PaymentConstants {
    public static final String PAYMENT_BASE_URL = "https://api.paystack.co";
    public static final String INITIALIZE_TRANSACTION_URL = PAYMENT_BASE_URL + "/transaction/initialize";
    public static final String VERIFY_TRANSACTION_URL = PAYMENT_BASE_URL + "transaction/verify/";
    public static final String LIST_TRANSACTIONS_URL = PAYMENT_BASE_URL + "/transaction";
    public static final String FETCH_TRANSACTION_URL = PAYMENT_BASE_URL + "/transaction/";


}
