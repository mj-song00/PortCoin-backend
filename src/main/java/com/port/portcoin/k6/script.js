import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'http://host.docker.internal:8080';
const EMAIL = 'test@test.com';
const PASSWORD =  'Asdf1234!';

// options: 시나리오 병렬 실행
export const options = {
    scenarios: {
        main_page: {
            executor: 'constant-vus',
            exec: 'mainPage',
            vus: 5,
            duration: '1m',
        },
        profit_rate: {
            executor: 'constant-vus',
            exec: 'profitRate',
            vus: 5,
            duration: '1m',
        },
    },
    httpTimeout: '2m',
};

//메인 페이지 조회 (토큰 불필요)
export function mainPage() {
    const res = http.get(`${BASE_URL}/api/v1/coin/price`);
    check(res, {
        'main status is 200': (r) => r.status === 200,
    });
}


//수익률 계산 (토큰 필요)
export function profitRate() {
    // 1. 로그인 요청
    const loginRes = http.post(`${BASE_URL}/api/v1/users/auth/sign-in`, JSON.stringify({
        email: EMAIL,
        password: PASSWORD,
    }), {
        headers: { 'Content-Type': 'application/json' },
    });
    const accessToken = loginRes.body?.trim();

    check(loginRes, {
        'token exists': (r) => typeof r.body === 'string' && r.body.length > 0
    });

    // 2. 수익률 요청
    const profitRes = http.get(`${BASE_URL}/api/v1/portfolio-coins/1025`, {
        headers: {
            Authorization: accessToken || '',
        },
    });

    check(profitRes, {
        'main status is 200': (r) => r.status === 200,  // HTTP 상태코드 200인지 확인
        'profit list is array': (r) => {
            try {
                const data = JSON.parse(r.body);
                return Array.isArray(data);
            } catch {
                return false;
            }
        },
    });
}
