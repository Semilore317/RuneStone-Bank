import { useState, useEffect } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { ArrowUpRight, ArrowDownRight, Clock, XCircle, CheckCircle2, RefreshCw } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { fetchRecentTransactions, parseTransactionDate, type Transaction } from '../../services/dashboard';

export function RecentTransactions() {
    const { user } = useAuth();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const loadTransactions = async () => {
        if (!user?.accountNumber) return;
        setIsLoading(true);
        try {
            const data = await fetchRecentTransactions(user.accountNumber);
            // Assuming data is an array of transactions. Limit to 5 for dashboard
            setTransactions(data.slice(0, 5));
        } catch (error) {
            console.error("Failed to fetch transactions:", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadTransactions();
    }, [user?.accountNumber]);

    return (
        <Card className="bg-zinc-900 border-4 border-zinc-800 text-white h-full flex flex-col">
            <CardHeader className="border-b-4 border-zinc-800 pb-4 flex flex-row items-center justify-between">
                <CardTitle className="text-xl md:text-2xl text-white">Recent Transactions</CardTitle>
                <button onClick={loadTransactions} className={`p-1 hover:text-white text-zinc-400 transition-colors ${isLoading ? 'animate-spin' : ''}`}>
                    <RefreshCw size={18} />
                </button>
            </CardHeader>
            <CardContent className="pt-0 flex-1 overflow-y-auto">
                {transactions.length === 0 && !isLoading ? (
                    <div className="flex items-center justify-center h-full min-h-[150px] text-zinc-500 font-bold uppercase tracking-widest text-sm">
                        No recent transactions
                    </div>
                ) : (
                    <div className="divide-y-4 divide-zinc-800">
                        {transactions.map((tx) => (
                            <TransactionItem key={tx.transactionId} transaction={tx} />
                        ))}
                    </div>
                )}
            </CardContent>
        </Card>
    );
}

function TransactionItem({ transaction }: { transaction: Transaction }) {
    // Determine visual style based on positive/negative cash flow 
    // CREDIT is positive. TRANSFER can be outgoing (negative) or incoming (positive).
    // In our backend, we use CREDIT for incoming transfers and TRANSFER for outgoing transfers.
    const isPositive = transaction.transactionType === 'CREDIT';

    const amountColor = isPositive ? 'text-green-400' : 'text-red-500';
    const amountPrefix = isPositive ? '+' : '-';

    const parsedDate = parseTransactionDate(transaction.timeOfCreation);

    const renderStatusIcon = () => {
        switch (transaction.status) {
            case 'SUCCESS':
                return <CheckCircle2 size={16} className="text-zinc-500" />;
            case 'PENDING':
                return <Clock size={16} className="text-zinc-500" />;
            case 'FAILED':
                return <XCircle size={16} className="text-red-500" />;
        }
    };

    return (
        <div className="py-6 flex items-center justify-between group">
            <div className="flex items-center gap-4">
                <div className={`p-3 border-2 border-zinc-800 bg-zinc-950 ${isPositive ? 'text-green-400' : 'text-red-500'}`}>
                    {isPositive ? <ArrowDownRight size={24} /> : <ArrowUpRight size={24} />}
                </div>
                <div>
                    <h4 className="font-bold text-lg">
                        {transaction.transactionType === 'TRANSFER' && transaction.counterpartyName
                            ? `Transfer to ${transaction.counterpartyName}`
                            : transaction.transactionType === 'CREDIT' && transaction.counterpartyName
                                ? `Transfer from ${transaction.counterpartyName}`
                                : transaction.transactionType}
                    </h4>
                    <div className="flex items-center gap-2 text-xs md:text-sm text-zinc-500 font-mono tracking-wider mt-1">
                        <span>{parsedDate.toLocaleDateString()}</span>
                        <span>•</span>
                        <span className="truncate max-w-[100px] md:max-w-none">{transaction.transactionId}</span>
                    </div>
                </div>
            </div>

            <div className="text-right">
                <div className={`font-black text-xl md:text-2xl ${amountColor} flex items-center justify-end gap-2`}>
                    {amountPrefix}${transaction.amount.toFixed(2)}
                </div>
                <div className="flex items-center justify-end gap-1 mt-1 font-bold text-xs tracking-wider uppercase">
                    {renderStatusIcon()}
                    <span className={transaction.status === 'FAILED' ? 'text-red-500' : 'text-zinc-500'}>
                        {transaction.status}
                    </span>
                </div>
            </div>
        </div>
    );
}
