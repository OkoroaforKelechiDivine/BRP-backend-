package com.project.BRP_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public class UserResponseDTO {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String address;
    private String gender;
    @JsonProperty("is_verified")
    private boolean isVerified;

}
