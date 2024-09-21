package com.project.BRP_backend.event.user;

import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.service.shared.SequenceGeneratorService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@AllArgsConstructor
public class UserModelListener extends AbstractMongoEventListener<User> {


    SequenceGeneratorService generatorService;
    @Override
    public void onBeforeConvert(BeforeConvertEvent<User> event){
        if (event.getSource().getId().intValue() < 1) {
            event.getSource().setId(BigInteger.valueOf(generatorService.generateSequence(User.SEQUENCE_NAME)));
        }
    }

}
