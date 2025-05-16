
SELECT COUNT(*) FROM users;

-- 3,6,9,12개월 가입자수
SELECT
    COUNT(*) AS total,
    '3 months' AS period  -- 기간
FROM users
WHERE created_at >= CURRENT_DATE - INTERVAL '3 months'
UNION ALL
SELECT
    COUNT(*) AS total,
    '6 months' AS period
FROM users
WHERE created_at >= CURRENT_DATE - INTERVAL '6 months'
UNION ALL
SELECT
    COUNT(*) AS total,
    '9 months' AS period
FROM users
WHERE created_at >= CURRENT_DATE - INTERVAL '9 months'
UNION ALL
SELECT
    COUNT(*) AS total,
    '12 months' AS period
FROM users
WHERE created_at >= CURRENT_DATE - INTERVAL '12 months';

SELECT
    DATE_TRUNC('week', created_at) AS week_start,
    COUNT(*) AS signup_count
FROM users
WHERE created_at >= CURRENT_DATE - INTERVAL '12 weeks' --오늘을 기준으로 12주전
GROUP BY week_start
ORDER BY week_start;


-- 툭정 일 기준 조회
WITH RECURSIVE week_ranges AS (
    -- 기준일 설정
    SELECT
        DATE '2025-01-05' AS week_start,
        (DATE '2025-01-05' + INTERVAL '6 days')::date AS week_end
    UNION ALL
    -- 다음 주 추가 (타입 통일 위해 캐스팅)
    SELECT
        (week_start + INTERVAL '7 days')::date,
        (week_end + INTERVAL '7 days')::date
    FROM week_ranges
    WHERE week_start + INTERVAL '7 days' <= CURRENT_DATE
),
               signup_counts AS (
                   SELECT
                       created_at::date AS signup_date
                   FROM users
                   WHERE created_at::date >= DATE '2025-01-05'
               )
SELECT
    w.week_start,
    w.week_end,
    COUNT(s.signup_date) AS signup_count
FROM week_ranges w
         LEFT JOIN signup_counts s
                   ON s.signup_date BETWEEN w.week_start AND w.week_end
GROUP BY w.week_start, w.week_end
ORDER BY w.week_start;


--월별 가입자수
-- 기준 월 교체시 current_date를 DATE '2025-04-01' 또는 '2025-04-01'::date로 변경
WITH months AS (
    SELECT DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month' * i AS month
    FROM generate_series(0, 11) AS s(i)
)
SELECT
    m.month,
    COUNT(u.id) AS signup_count
FROM months m
         LEFT JOIN users u ON DATE_TRUNC('month', u.created_at) = m.month
GROUP BY m.month
ORDER BY m.month;
