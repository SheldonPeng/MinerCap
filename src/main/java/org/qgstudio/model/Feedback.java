package org.qgstudio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 反馈信息
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors
public class Feedback {

    private boolean rescue;
    private boolean locate;
    private String address;
    private String message;
}
