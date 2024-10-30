package com.project.BRP_backend.service;

import com.project.BRP_backend.configuration.SMSConfiguration;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSService {
    private final SMSConfiguration smsConfiguration;
    @Async
    public void sendMessage(String smsMessage, String recipientNo) {
        Twilio.init(smsConfiguration.getAccount_Sid(),smsConfiguration.getAuth_token());
        Message message = Message.creator(new PhoneNumber(recipientNo),
                new PhoneNumber(smsConfiguration.getPhone_Number()),
                smsMessage)
                .create();
        //Some logging...

    }
}
