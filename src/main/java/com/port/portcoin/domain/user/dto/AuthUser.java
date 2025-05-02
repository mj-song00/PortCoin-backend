package com.port.portcoin.domain.user.dto;

import com.port.portcoin.domain.user.enums.UserRole;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthUser {
    private final UUID id;
    private final String nickname;
    private final UserRole role;

    public AuthUser(UUID id, String nickname, UserRole role){
        this.id = id;
        this.nickname = nickname;
        this.role = role;
    }
}
