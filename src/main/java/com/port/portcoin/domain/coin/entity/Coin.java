package com.port.portcoin.domain.coin.entity;


import com.port.portcoin.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "coin")
@Getter
public class Coin extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol; // 예: BTC, ETH

    @Column(nullable = false)
    private String name; // 예: Bitcoin, Ethereum

    public Coin(String symbol,String name){
        this.symbol = symbol;
        this.name = name;
    }
}
