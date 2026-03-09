import { apiRequest } from './api';

export interface AccountInfo {
    accountName: string;
    accountBalance: number;
    accountNumber: string;
}

/** Session duration in milliseconds — must match backend JWT_EXPIRATION */
export const SESSION_DURATION_MS = 15 * 60 * 1000; // 15 minutes

export interface LoginResponse {
    responseCode: string;
    responseMessage: string;
    accountInfo: AccountInfo;
    jwt: string;
}

export async function loginUser(email: string, password: string): Promise<LoginResponse> {
    const response = await apiRequest('/user/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
    });

    if (response.jwt) {
        localStorage.setItem('jwt', response.jwt);
        localStorage.setItem('accountInfo', JSON.stringify(response.accountInfo));
        localStorage.setItem('loginTimestamp', Date.now().toString());
    }

    return response as LoginResponse;
}

export function logoutUser(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('accountInfo');
    localStorage.removeItem('loginTimestamp');
}

export function getToken(): string | null {
    return localStorage.getItem('jwt');
}

export function getStoredAccountInfo(): AccountInfo | null {
    const stored = localStorage.getItem('accountInfo');
    if (!stored) return null;
    try {
        return JSON.parse(stored);
    } catch {
        return null;
    }
}

export function isAuthenticated(): boolean {
    return !!getToken() && !isTokenExpired();
}

export function getLoginTimestamp(): number | null {
    const ts = localStorage.getItem('loginTimestamp');
    return ts ? parseInt(ts, 10) : null;
}

export function isTokenExpired(): boolean {
    const ts = getLoginTimestamp();
    if (!ts) return true;
    return Date.now() - ts > SESSION_DURATION_MS;
}
