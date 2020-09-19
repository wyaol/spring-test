package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Trade {
    private Integer userId;
    private Integer eventId;
    private Integer rank;
    private Integer amount;
}
