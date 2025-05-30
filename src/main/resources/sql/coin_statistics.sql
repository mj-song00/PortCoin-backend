SELECT c.symbol, SUM(pc.amount) AS sum
FROM portfolio_coin pc
         JOIN coin c ON pc.coin_id = c.id
         JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
         JOIN users u ON p.user_id = u.id
GROUP BY c.symbol
ORDER BY sum DESC;


SELECT
    CASE
        WHEN coin_count = 1 THEN '1개'
        WHEN coin_count BETWEEN 2 AND 3 THEN '2~3개'
        WHEN coin_count BETWEEN 4 AND 5 THEN '4~5개'
        ELSE '6개 이상'
        END AS 구간,
    COUNT(*) AS 유저수
FROM (
         SELECT u.id AS user_id, COUNT(DISTINCT pc.coin_id) AS coin_count
         FROM users u
                  JOIN portfolio p ON u.id = p.user_id
                  JOIN portfolio_coin pc ON p.portfolio_id = pc.portfolio_id
         GROUP BY u.id
     ) AS user_coin_counts
GROUP BY 구간;

--전체유저 평균 수익률
-- SELECT ROUND (AVG(user_profit.profit_rate) * 100, 2) AS average_profit_rate
-- FROM (
--          SELECT u.id AS user_id,
--                 CASE
--                     WHEN SUM(pc.purchase_price * pc.amount) = 0 THEN 0
--                     ELSE ROUND(
--                             ((SUM(pc.current_price * pc.amount) - SUM(pc.purchase_price * pc.amount))
--                                 / SUM(pc.purchase_price * pc.amount))::numeric,2
--                          )
--                     END AS profit_rate
--          FROM users u
--                   JOIN portfolio p ON u.id = p.user_id
--                   JOIN portfolio_coin pc ON p.portfolio_id = pc.portfolio_id
--          GROUP BY u.id
--      ) AS user_profit;
-- 공통 CTE
WITH user_profit AS (
    SELECT u.id AS user_id,
           CASE
               WHEN SUM(pc.purchase_price * pc.amount) = 0 THEN 0
               ELSE ROUND(
                       ((SUM(pc.current_price * pc.amount) - SUM(pc.purchase_price * pc.amount))
                           / SUM(pc.purchase_price * pc.amount))::numeric, 4
                    )
               END AS profit_rate
    FROM users u
             JOIN portfolio p ON u.id = p.user_id
             JOIN portfolio_coin pc ON p.portfolio_id = pc.portfolio_id
    GROUP BY u.id
)
-- 평균 수익률
SELECT ROUND(AVG(profit_rate) * 100, 2) AS average_profit_rate
FROM user_profit;

-- 상위 5% 유저
WITH user_profit AS (  -- 위 쿼리 그대로
    SELECT
        u.id AS user_id,
        CASE
            WHEN SUM(pc.purchase_price * pc.amount) = 0 THEN 0
            ELSE ROUND(
                    (
                        (SUM(pc.current_price * pc.amount) - SUM(pc.purchase_price * pc.amount))
                            / SUM(pc.purchase_price * pc.amount)
                        )::numeric, 4)
            END AS profit_rate
    FROM users u
             JOIN portfolio p ON u.id = p.user_id
             JOIN portfolio_coin pc ON p.portfolio_id = pc.portfolio_id
    GROUP BY u.id
)
SELECT up.user_id, up.profit_rate, p.portfolio_id
FROM (
         SELECT user_id, profit_rate,
                PERCENT_RANK() OVER (ORDER BY profit_rate) AS percentile
         FROM user_profit
     ) up
         JOIN portfolio p ON up.user_id = p.user_id
WHERE up.percentile >= 0.95
ORDER BY profit_rate DESC;

-- 하위 10%유저
WITH user_profit AS (
    SELECT
        u.id AS user_id,
        CASE
            WHEN SUM(pc.purchase_price * pc.amount) = 0 THEN 0
            ELSE ROUND(
                    (
                        (SUM(pc.current_price * pc.amount) - SUM(pc.purchase_price * pc.amount))
                            / SUM(pc.purchase_price * pc.amount)
                        )::numeric, 4)
            END AS profit_rate
    FROM users u
             JOIN portfolio p ON u.id = p.user_id
             JOIN portfolio_coin pc ON p.portfolio_id = pc.portfolio_id
    GROUP BY u.id
)
SELECT up.user_id, up.profit_rate, p.portfolio_id
FROM (
         SELECT user_id, profit_rate,
                PERCENT_RANK() OVER (ORDER BY profit_rate) AS percentile
         FROM user_profit
     ) up
         JOIN portfolio p ON up.user_id = p.user_id
WHERE up.percentile <= 0.10
ORDER BY profit_rate DESC;
