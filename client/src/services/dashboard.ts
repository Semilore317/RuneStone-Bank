import { apiRequest } from './api';
import type { AccountInfo } from './auth';

export interface BalanceResponse {
    responseCode: string;
    responseMessage: string;
    accountInfo: AccountInfo;
}

export interface Transaction {
    transactionId: string;
    transactionType: 'CREDIT' | 'DEBIT' | 'TRANSFER';
    amount: number;
    accountNumber: string;
    status: 'PENDING' | 'FAILED' | 'SUCCESS';
    timeOfCreation: string | number[];
    counterpartyName?: string;
    counterpartyAccountNumber?: string;
}

export function parseTransactionDate(dateVal: string | number[]): Date {
    if (Array.isArray(dateVal)) {
        const [year, month, day, hour = 0, minute = 0, second = 0] = dateVal;
        return new Date(year, month - 1, day, hour, minute, second);
    }
    return new Date(dateVal);
}

export async function fetchBalance(accountNumber: string): Promise<BalanceResponse> {
    return apiRequest<BalanceResponse>(`/api/v1/user/balanceEnquiry?accountNumber=${accountNumber}`, {
        method: 'GET',
    }) as Promise<BalanceResponse>;
}

export async function fetchRecentTransactions(accountNumber: string): Promise<Transaction[]> {
    const response = await apiRequest<Transaction[]>(`/api/v1/transactions/history?accountNumber=${accountNumber}`, {
        method: 'GET',
    });
    return (response.data ?? response) as unknown as Transaction[];
}

export interface NameEnquiryResponse {
    responseCode: string;
    responseMessage: string;
    accountInfo: {
        accountName: string;
        accountNumber: string;
        accountBalance: number;
    } | null;
}

export async function lookupAccountName(accountNumber: string): Promise<NameEnquiryResponse> {
    return apiRequest<NameEnquiryResponse>(`/api/v1/user/nameEnquiry?accountNumber=${accountNumber}`, {
        method: 'GET',
    }) as Promise<NameEnquiryResponse>;
}

export interface TransferRequest {
    sender: string;
    receiver: string;
    amount: number;
}

export async function transferFunds(request: TransferRequest): Promise<void> {
    await apiRequest('/api/v1/transactions/transfer', {
        method: 'POST',
        body: JSON.stringify(request)
    });
}

export interface UserProfileResponse {
    firstName: string;
    lastName: string;
    email: string;
    accountNumber: string;
    emailNotifs: boolean;
    loginAlerts: boolean;
    transferAlerts: boolean;
}

export interface ProfileUpdateRequest {
    firstName: string;
    lastName: string;
    email: string;
    emailNotifs: boolean;
    loginAlerts: boolean;
    transferAlerts: boolean;
}

export interface PasswordUpdateRequest {
    currentPassword?: string;
    newPassword?: string;
}

export async function fetchProfile(accountNumber: string): Promise<UserProfileResponse> {
    const response = await apiRequest<UserProfileResponse>(`/api/v1/user/profile?accountNumber=${accountNumber}`, {
        method: 'GET'
    });
    if (!response.data) throw new Error("Profile not found");
    return response.data;
}

export async function updateProfile(accountNumber: string, request: ProfileUpdateRequest): Promise<void> {
    await apiRequest(`/api/v1/user/profile?accountNumber=${accountNumber}`, {
        method: 'PUT',
        body: JSON.stringify(request)
    });
}

export async function updatePassword(accountNumber: string, request: PasswordUpdateRequest): Promise<void> {
    await apiRequest(`/api/v1/user/password?accountNumber=${accountNumber}`, {
        method: 'PUT',
        body: JSON.stringify(request)
    });
}

export async function requestStatement(accountNumber: string, startDate: string, endDate: string): Promise<{ message: string }> {
    const response = await apiRequest<{ message: string }>(`/api/v1/bankstatement/email?accountNumber=${accountNumber}&start=${startDate}&end=${endDate}`, {
        method: 'POST',
    });
    return response.data ?? response;
}
