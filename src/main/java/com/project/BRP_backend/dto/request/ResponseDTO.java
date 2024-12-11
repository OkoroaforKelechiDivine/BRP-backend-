package com.project.BRP_backend.dto.request;


import com.project.BRP_backend.dto.response.UserResponseDTO;
import com.project.BRP_backend.model.user.User;
import lombok.*;


@Builder
@Data
public class ResponseDTO {

    private UserResponseDTO user;

    private String token;

}