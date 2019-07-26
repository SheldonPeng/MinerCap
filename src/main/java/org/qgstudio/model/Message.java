package org.qgstudio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 传输消息的实体类对象
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    private String address;
    private String data;
    private boolean status;
}
