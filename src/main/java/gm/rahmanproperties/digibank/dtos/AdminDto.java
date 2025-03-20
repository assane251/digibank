package gm.rahmanproperties.digibank.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDto {
    private Long id;
    private String username;
    private String password;
    private String role;
    private boolean firstLogin;
    private List<String> tickets;
}
