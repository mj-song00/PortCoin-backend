package com.port.portcoin.domain.portfolio.entity;

import com.port.portcoin.common.entity.Timestamped;
import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import com.port.portcoin.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "portfolio")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)

public class Portfolio extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioCoin> portfolioCoins = new ArrayList<>();

    private LocalDateTime deletedAt;

    public Portfolio(String name, User user){
        this.name = name;
        this.user = user;
    }

    public void updateName(@NotBlank(message = "새로운 이름을 입력해주세요.") String name) {
        this.name = name;
    }

    public void updateDeletedAt() {
        this.deletedAt =  LocalDateTime.now();
    }
}
