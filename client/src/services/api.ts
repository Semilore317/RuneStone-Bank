const BASE_URL = '';

export interface ApiResponse<T = unknown> {
    responseCode: string;
    responseMessage: string;
    accountInfo?: {
        accountName: string;
        accountBalance: number;
        accountNumber: string;
    };
    jwt?: string;
    data?: T;
}

export async function apiRequest<T = unknown>(
    endpoint: string,
    options: RequestInit = {}
): Promise<ApiResponse<T>> {
    const token = localStorage.getItem('jwt');

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        ...((options.headers as Record<string, string>) || {}),
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (!response.ok) {
        if (response.status === 401) {
            // Token expired or invalid
            localStorage.removeItem('jwt');
            localStorage.removeItem('accountInfo');
            window.location.href = '/login';
            throw new Error('Session expired. Please log in again.');
        }

        const errorBody = await response.json().catch(() => null);
        throw new Error(
            errorBody?.responseMessage || `Request failed with status ${response.status}`
        );
    }

    return response.json();
}
