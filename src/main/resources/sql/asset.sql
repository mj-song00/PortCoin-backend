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

-- 자산별 상위 10%
SELECT
    p.user_id,
    SUM(pc.amount * pc.current_price) AS total_asset
FROM
    portfolio_coin pc
        JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
GROUP BY
    p.user_id;
WITH user_assets AS (
    SELECT
        p.user_id,
        SUM(pc.amount * pc.current_price) AS total_asset
    FROM
        portfolio_coin pc
            JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
    GROUP BY
        p.user_id
),
     ranked_users AS (
         SELECT *,
                PERCENT_RANK() OVER (ORDER BY total_asset DESC) AS rank_percent
         FROM user_assets
     )
SELECT *
FROM ranked_users
WHERE rank_percent <= 0.1;



-- 하위 10%
SELECT
    p.user_id,
    SUM(pc.amount * pc.current_price) AS total_asset
FROM
    portfolio_coin pc
        JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
GROUP BY
    p.user_id;
WITH user_assets AS (
    SELECT
        p.user_id,
        SUM(pc.amount * pc.current_price) AS total_asset
    FROM
        portfolio_coin pc
            JOIN portfolio p ON pc.portfolio_id = p.portfolio_id
    GROUP BY
        p.user_id
),
     ranked_users AS (
         SELECT *,
                PERCENT_RANK() OVER (ORDER BY total_asset ASC) AS rank_percent
         FROM user_assets
     )
SELECT *
FROM ranked_users
WHERE rank_percent <= 0.1;
