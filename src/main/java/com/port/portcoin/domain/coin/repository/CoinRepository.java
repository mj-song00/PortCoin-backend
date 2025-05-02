package com.port.portcoin.domain.coin.repository;

import com.port.portcoin.domain.coin.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CoinRepository extends JpaRepository<Coin, Long> {
}
