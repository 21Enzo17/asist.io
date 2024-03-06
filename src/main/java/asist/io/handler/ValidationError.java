package asist.io.handler;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidationError {
    private String field;
    private String message;
}