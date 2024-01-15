package project.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Token {

    private String email;

    private String accessToken;

    private String refreshToken;

    public Token(User user) {
        this.email = user.getEmail();
    }
}
