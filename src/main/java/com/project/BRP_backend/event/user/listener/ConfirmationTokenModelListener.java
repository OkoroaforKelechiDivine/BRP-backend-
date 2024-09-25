package com.project.BRP_backend.event.user.listener;

import com.project.BRP_backend.model.security.ConfirmationToken;
import com.project.BRP_backend.service.shared.SequenceGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@AllArgsConstructor
public class ConfirmationTokenModelListener extends AbstractMongoEventListener<ConfirmationToken> {
    SequenceGeneratorService sequenceGeneratorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<ConfirmationToken> event) {
        if (event.getSource().getId().intValue() < 1) {
            event.getSource().setId(BigInteger.valueOf(sequenceGeneratorService.generateSequence(ConfirmationToken.SEQUENCE_NAME)));
        }
    }
}
