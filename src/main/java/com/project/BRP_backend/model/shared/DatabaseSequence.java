package com.project.BRP_backend.model.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@AllArgsConstructor
@Getter
@Setter
public class DatabaseSequence {
    @Id
    private String id;

    private long seq;

}
