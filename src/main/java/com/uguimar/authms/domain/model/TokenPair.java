package com.uguimar.authms.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPair {
    private Token accessToken;
    private Token refreshToken;
}
