import { apiRequest } from './api';

export interface AccountInfo {
    accountName: string;
    accountBalance: number;
    accountNumber: string;
}

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
    }

    return response as LoginResponse;
}

export function logoutUser(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('accountInfo');
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
    return !!getToken();
}
