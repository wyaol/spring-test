package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "trade")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TradeDto {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;
    private Integer eventId;
    private Integer rank;
    private Integer amount;
}
