import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { ArrowUpRight, ArrowDownRight, Clock, XCircle, CheckCircle2 } from 'lucide-react';

interface Transaction {
    transactionId: string;
    transactionType: 'CREDIT' | 'DEBIT' | 'TRANSFER';
    amount: number;
    accountNumber: string;
    status: 'PENDING' | 'FAILED' | 'SUCCESS';
    timeOfCreation: string; // ISO string 
}

const mockTransactions: Transaction[] = [
    {
        transactionId: "TXN-982374-ABCD",
        transactionType: "CREDIT",
        amount: 5200.00,
        accountNumber: "0000000000",
        status: "SUCCESS",
        timeOfCreation: "2026-03-03T10:15:00Z"
    },
    {
        transactionId: "TXN-102938-EFGH",
        transactionType: "DEBIT",
        amount: 154.99,
        accountNumber: "1234567890",
        status: "SUCCESS",
        timeOfCreation: "2026-03-02T16:45:00Z"
    },
    {
        transactionId: "TXN-564738-IJKL",
        transactionType: "TRANSFER",
        amount: 50.00,
        accountNumber: "9876543210",
        status: "PENDING",
        timeOfCreation: "2026-03-01T09:30:00Z"
    },
    {
        transactionId: "TXN-112233-MNOP",
        transactionType: "DEBIT",
        amount: 890.00,
        accountNumber: "4561237890",
        status: "FAILED",
        timeOfCreation: "2026-02-28T14:20:00Z"
    }
];

export function RecentTransactions() {
    return (
        <Card className="bg-zinc-900 border-4 border-zinc-800 text-white h-full">
            <CardHeader className="border-b-4 border-zinc-800 pb-4">
                <CardTitle className="text-xl md:text-2xl text-white">Recent Transactions</CardTitle>
            </CardHeader>
            <CardContent className="pt-0">
                <div className="divide-y-4 divide-zinc-800">
                    {mockTransactions.map((tx) => (
                        <TransactionItem key={tx.transactionId} transaction={tx} />
                    ))}
                </div>
            </CardContent>
        </Card>
    );
}

function TransactionItem({ transaction }: { transaction: Transaction }) {
    // Determine visual style based on positive/negative cash flow
    const isPositive = transaction.transactionType === 'CREDIT';

    const amountColor = isPositive ? 'text-green-400' : 'text-red-500';
    const amountPrefix = isPositive ? '+' : '-';

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
                    <h4 className="font-bold text-lg">{transaction.transactionType}</h4>
                    <div className="flex items-center gap-2 text-xs md:text-sm text-zinc-500 font-mono tracking-wider mt-1">
                        <span>{new Date(transaction.timeOfCreation).toLocaleDateString()}</span>
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
