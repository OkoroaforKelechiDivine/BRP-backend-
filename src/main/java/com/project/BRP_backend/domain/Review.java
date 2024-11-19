package com.project.BRP_backend.domain;

import com.project.BRP_backend.model.user.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Review {
    private String review;
    private int rating;
    private LocalDate date;
    private String reviewer;
}
