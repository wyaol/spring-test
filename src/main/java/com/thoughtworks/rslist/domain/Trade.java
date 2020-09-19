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

    public Trade(Integer userId, Integer rank, Integer amount) {
        this.userId = userId;
        this.rank = rank;
        this.amount = amount;
    }
}
