package com.cloud.mail.api.usr.model;


import lombok.Data;

@Data
public class LoginDto {
    private String name;
    private String password;
}
