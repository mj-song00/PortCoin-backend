package com.port.portcoin.domain.portfolio;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.coin.entity.Coin;
import com.port.portcoin.domain.coin.repository.CoinRepository;
import com.port.portcoin.domain.portfolio.dto.request.PortfolioCoinRequestDto;
import com.port.portcoin.domain.portfolio.dto.request.PortfolioRequest;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioDetailResponse;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioResponse;
import com.port.portcoin.domain.portfolio.entity.Portfolio;
import com.port.portcoin.domain.portfolio.repository.PortfolioRepository;
import com.port.portcoin.domain.portfolio.service.PortfolioServiceImpl;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.enums.UserRole;
import com.port.portcoin.domain.user.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoinRepository coinRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    private Coin coin;
    private PortfolioRequest request;
    private AuthUser authUser;
    private User user;
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        // 기본 설정
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User("test@test.com", "tester", "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        authUser = new AuthUser(user.getId(), "test@test.com", UserRole.USER);

        coin = new Coin("btc", "비트코인");

        // 요청 DTO 생성
        PortfolioCoinRequestDto coinDto = new PortfolioCoinRequestDto(
                1L,
                10.0,
                250000.0,
                "2025-06-24"
        );

        request = new PortfolioRequest("테스트 포트폴리오", List.of(coinDto));

    }

    @Test
    @DisplayName("포트폴리오 생성 - 성공")
    void createPortfolioSuccess() {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(coinRepository.findById(1L)).willReturn(Optional.of(coin));
        given(portfolioRepository.save(any(Portfolio.class))).willReturn(mock(Portfolio.class));

        // when
        portfolioService.createPortfolio(request, authUser);

        // then
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
        verify(userRepository).findById(user.getId());
        verify(coinRepository).findById(1L);
    }

    @Test
    @DisplayName("프토폴리오 생성 실패 - 유저 없음")
    void userNotFound(){
        // given
        given(userRepository.findById(any())).willReturn(Optional.empty());

        // when
        BaseException exception = assertThrows(BaseException.class, () -> {
            portfolioService.createPortfolio(request, authUser);
        });

        // then
        assertEquals(ExceptionEnum.USER_NOT_FOUND, exception.getExceptionEnum());
    }

    @Test
    @DisplayName("포트폴리오 생성 실패 - 제목 미입력")
    void titleNotEntered(){
        // given
        PortfolioRequest request = new PortfolioRequest("   ", List.of());

        // when
        Set<ConstraintViolation<PortfolioRequest>> violations = validator.validate(request);

        // then
        for (ConstraintViolation<PortfolioRequest> violation : violations) {
            System.out.println(violation.getMessage());
        }
    }

    @Test
    @DisplayName("포트폴리오 생성 실패 - 코인 없음")
    void coinNotFound(){
        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(coinRepository.findById(1L)).willReturn(Optional.empty());

        //when
        BaseException exception = assertThrows(BaseException.class, () -> {
            portfolioService.createPortfolio(request, authUser);
        });

        //than
        assertEquals(ExceptionEnum.COIN_NOT_FOUND, exception.getExceptionEnum());

    }

    @Test
    @DisplayName("포트폴리오 전체 조회 - 성공")
    void portfoliosGetSuccess(){
        //given
        Portfolio portfolio = new Portfolio("테스트 포트폴리오", user);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(portfolioRepository.findAllByUserIdAndDeletedAtIsNull(user.getId()))
                .willReturn(List.of(portfolio));

        //when
        List<PortfolioResponse> result = portfolioService.getPortfolios(authUser);

        //than
        assertEquals(1, result.size());
        assertEquals("테스트 포트폴리오", result.get(0).getName());

        verify(userRepository).findById(user.getId());
        verify(portfolioRepository).findAllByUserIdAndDeletedAtIsNull(user.getId());
    }

    @Test
    @DisplayName("포트폴리오 상세조회 - 성공")
    void FailedToViewPortfolioDetails(){
        //given
        Portfolio portfolio = new Portfolio("상세조회 포트폴리오", user);
        ReflectionTestUtils.setField(portfolio, "createdAt", LocalDateTime.now());
        given(portfolioRepository.findByPortfolioId(1L))
                .willReturn(Optional.of(portfolio));

        // when
        PortfolioDetailResponse response = portfolioService.getDetail(1L, authUser);

        // then
       assertEquals("상세조회 포트폴리오", response.getName());
       verify(portfolioRepository).findByPortfolioId(1L);
    }

    @Test
    @DisplayName("포트폴리오 상세조회 실패 - 포트폴리오 없음")
    void noPortfolio(){
        //given
        Portfolio portfolio = new Portfolio("상세조회 포트폴리오", user);
        ReflectionTestUtils.setField(portfolio, "createdAt", LocalDateTime.now());
        given(portfolioRepository.findByPortfolioId(1L))
                .willReturn(Optional.empty());

        // when
        BaseException exception = assertThrows(BaseException.class, () -> {
            portfolioService.getDetail( 1L,authUser);
        });

        //than
        assertEquals(ExceptionEnum.PORTFOLIO_NOT_FOUND, exception.getExceptionEnum());
    }

    @Test
    @DisplayName("포트폴리오 상세조회 실패 - 유저 불일치")
    void notOwner(){
        // given
        Portfolio portfolio = new Portfolio("상세조회 포트폴리오", user);
        ReflectionTestUtils.setField(portfolio, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(portfolio, "portfolioId", 1L);

        // 유저 불일치 상황: 다른 UUID
        UUID differentUserId = UUID.randomUUID();
        AuthUser otherAuthUser = new AuthUser(differentUserId, "hacker@test.com", UserRole.USER);

        given(portfolioRepository.findByPortfolioId(1L))
                .willReturn(Optional.of(portfolio));

        // when
        BaseException exception = assertThrows(BaseException.class, () -> {
            portfolioService.getDetail(1L, otherAuthUser);
        });

        // then
        assertEquals(ExceptionEnum.USER_NOT_FOUND, exception.getExceptionEnum());
    }

    @Test
    @DisplayName("포트폴리오 상세조회 실패 - 삭제된 포트폴리오")
    void deletedPortfolio() {
        // given
        Portfolio portfolio = new Portfolio("삭제된 포트폴리오", user);
        ReflectionTestUtils.setField(portfolio, "portfolioId", 1L);
        ReflectionTestUtils.setField(portfolio, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(portfolio, "deletedAt", LocalDateTime.now()); // ✅ 삭제됨 표시

        given(portfolioRepository.findByPortfolioId(1L))
                .willReturn(Optional.of(portfolio));

        // when
        BaseException exception = assertThrows(BaseException.class, () -> {
            portfolioService.getDetail(1L, authUser);
        });

        // then
        assertEquals(ExceptionEnum.PORTFOLIO_NOT_FOUND, exception.getExceptionEnum());
    }
}
