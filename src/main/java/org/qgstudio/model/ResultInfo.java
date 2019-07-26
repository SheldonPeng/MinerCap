package org.qgstudio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Param:
 * @return:
 * @Author: SheldonPeng
 * @Date: 2019-07-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultInfo {

    private boolean status;
    private String msg;
    private Object data;}
