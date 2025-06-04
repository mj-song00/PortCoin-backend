-- 원형 차트
SELECT symbol, name, SUM(amount * current_price) as total_asset_value
FROM portfolio_coin pc
         JOIN coin c ON pc.coin_id = c.id
GROUP BY symbol, name
ORDER BY total_asset_value DESC
LIMIT 10;


-- 코인별 자산 합계
SELECT symbol, c.name,
       SUM(pc.amount) AS total_amount,
       AVG(pc.current_price) AS avg_current_price,
       SUM(pc.amount * pc.current_price) AS total_asset_value
FROM
    portfolio_coin pc
        JOIN
    coin c ON pc.coin_id = c.id
GROUP BY
    c.symbol, c.name
ORDER BY
    total_asset_value DESC;



-- 전체 유저 자산 합계
SELECT
    AVG(user_total) AS average_asset_value
FROM (
         SELECT
             SUM(pc.amount * pc.current_price) AS user_total
         FROM
             portfolio_coin pc
         GROUP BY
             pc.portfolio_id  -- 또는 유저 ID 기준으로 그룹핑
     ) AS user_totals;
