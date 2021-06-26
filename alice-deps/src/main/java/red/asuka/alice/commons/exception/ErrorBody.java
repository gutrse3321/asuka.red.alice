package red.asuka.alice.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 15:13
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorBody {

    private int code;
    private String message;
    private String throwType;
}
