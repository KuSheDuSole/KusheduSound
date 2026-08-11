package ru.kushedusound.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER,
    ADMIN;

    public SimpleGrantedAuthority toAuthority(){
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }
}
