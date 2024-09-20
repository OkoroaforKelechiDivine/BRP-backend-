package com.project.BRP_backend.controller.user;

import com.project.BRP_backend.domain.user.Address;
import com.project.BRP_backend.dto.user.UserDTO;
import com.project.BRP_backend.dto.user.UserRegistrationParams;
import com.project.BRP_backend.service.user.UserService;
import com.project.BRP_backend.shared.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

import static com.project.BRP_backend.shared.ResponseUtil.getResponse;

@RestController
@RequestMapping("api/v1/user/")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("register")
    public ResponseEntity<Response> registerUser(HttpServletRequest httpServletRequest, @RequestBody UserRegistrationParams userRegistrationParams){
        UserDTO userDTO = new UserDTO(
                userRegistrationParams.firstName(),
                userRegistrationParams.lastName(),
                userRegistrationParams.email(),
                userRegistrationParams.password(),
                new Address(userRegistrationParams.houseAddress(),
                        userRegistrationParams.city(),
                        userRegistrationParams.state(),
                        userRegistrationParams.country()),
                userRegistrationParams.phoneNumber(),
                ""
        );
        userDTO = userService.registerUser(userDTO);
        return (Objects.nonNull(userDTO)) ? new ResponseEntity<>(getResponse(httpServletRequest, Map.of("userInfo", userDTO), "user created successfully", HttpStatus.CREATED), HttpStatus.OK)
                :
                new ResponseEntity<>(getResponse(httpServletRequest, Map.of("userInfo", ""), "user already exists", HttpStatus.OK), HttpStatus.OK);
    }

}
