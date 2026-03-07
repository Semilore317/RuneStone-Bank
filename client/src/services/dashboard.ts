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
    timeOfCreation: string;
}

export async function fetchBalance(accountNumber: string): Promise<BalanceResponse> {
    return apiRequest<BalanceResponse>(`/user/balanceEnquiry?accountNumber=${accountNumber}`, {
        method: 'GET',
    }) as Promise<BalanceResponse>;
}

export async function fetchRecentTransactions(accountNumber: string): Promise<Transaction[]> {
    const token = localStorage.getItem('jwt');
    const response = await fetch(`/api/v1/transactions/history?accountNumber=${accountNumber}`, {
        method: 'GET',
        headers: token ? { 'Authorization': `Bearer ${token}` } : {}
    });

    if (!response.ok) {
        throw new Error('Failed to fetch transaction history');
    }
    return response.json();
}

export interface TransferRequest {
    sender: string;
    receiver: string;
    amount: number;
}

export async function transferFunds(request: TransferRequest): Promise<void> {
    await apiRequest('/transactions/transfer', {
        method: 'POST',
        body: JSON.stringify(request)
    });
}
