package com.expis.login.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedDto {
    private String payload;
}
