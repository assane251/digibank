package gm.rahmanproperties.optibank.service;

import gm.rahmanproperties.optibank.domain.Client;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserSession {
    private static UserSession instance;
    private Client loggedInClient;

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void clearSession() {
        loggedInClient = null;
    }
}
