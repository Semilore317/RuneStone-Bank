import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { ArrowUpRight, ArrowDownRight, Clock, XCircle, CheckCircle2, Search, Filter } from 'lucide-react';
import { Input } from '../components/ui/Input';

interface Transaction {
    transactionId: string;
    transactionType: 'CREDIT' | 'DEBIT' | 'TRANSFER';
    amount: number;
    accountNumber: string;
    status: 'PENDING' | 'FAILED' | 'SUCCESS';
    timeOfCreation: string;
}

const allTransactions: Transaction[] = [
    { transactionId: 'TXN-982374-ABCD', transactionType: 'CREDIT', amount: 5200.00, accountNumber: '0000000000', status: 'SUCCESS', timeOfCreation: '2026-03-06T10:15:00Z' },
    { transactionId: 'TXN-102938-EFGH', transactionType: 'DEBIT', amount: 154.99, accountNumber: '1234567890', status: 'SUCCESS', timeOfCreation: '2026-03-05T16:45:00Z' },
    { transactionId: 'TXN-564738-IJKL', transactionType: 'TRANSFER', amount: 50.00, accountNumber: '9876543210', status: 'PENDING', timeOfCreation: '2026-03-04T09:30:00Z' },
    { transactionId: 'TXN-112233-MNOP', transactionType: 'DEBIT', amount: 890.00, accountNumber: '4561237890', status: 'FAILED', timeOfCreation: '2026-03-03T14:20:00Z' },
    { transactionId: 'TXN-445566-QRST', transactionType: 'CREDIT', amount: 12000.00, accountNumber: '0000000000', status: 'SUCCESS', timeOfCreation: '2026-03-02T08:00:00Z' },
    { transactionId: 'TXN-778899-UVWX', transactionType: 'TRANSFER', amount: 320.00, accountNumber: '5566778899', status: 'SUCCESS', timeOfCreation: '2026-03-01T11:30:00Z' },
    { transactionId: 'TXN-998877-YZAB', transactionType: 'DEBIT', amount: 45.00, accountNumber: '1122334455', status: 'SUCCESS', timeOfCreation: '2026-02-28T19:45:00Z' },
    { transactionId: 'TXN-665544-CDEF', transactionType: 'CREDIT', amount: 800.00, accountNumber: '0000000000', status: 'SUCCESS', timeOfCreation: '2026-02-27T07:15:00Z' },
];

type FilterType = 'ALL' | 'CREDIT' | 'DEBIT' | 'TRANSFER';

export function TransactionsPage() {
    const [searchTerm, setSearchTerm] = useState('');
    const [filterType, setFilterType] = useState<FilterType>('ALL');

    const filtered = allTransactions.filter((tx) => {
        const matchesSearch = tx.transactionId.toLowerCase().includes(searchTerm.toLowerCase()) ||
            tx.accountNumber.includes(searchTerm);
        const matchesFilter = filterType === 'ALL' || tx.transactionType === filterType;
        return matchesSearch && matchesFilter;
    });

    return (
        <>
            <header className="mb-12">
                <h2 className="text-4xl md:text-5xl font-black uppercase tracking-tighter">Transactions</h2>
                <p className="text-zinc-500 font-bold uppercase tracking-widest text-sm mt-2">Full transaction history</p>
            </header>

            {/* Filters */}
            <div className="flex flex-col md:flex-row gap-4 mb-8">
                <div className="relative flex-1">
                    <Search size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500" />
                    <Input
                        type="text"
                        placeholder="Search by ID or account number..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="bg-zinc-900 border-4 border-zinc-800 text-white placeholder-zinc-600 pl-12 font-mono"
                    />
                </div>
                <div className="flex gap-2">
                    {(['ALL', 'CREDIT', 'DEBIT', 'TRANSFER'] as FilterType[]).map((type) => (
                        <button
                            key={type}
                            onClick={() => setFilterType(type)}
                            className={`px-4 py-3 border-4 font-bold uppercase text-sm tracking-wider transition-all
                                ${filterType === type
                                    ? 'bg-ochre-800 border-ochre-800 text-white'
                                    : 'bg-zinc-900 border-zinc-800 text-zinc-500 hover:text-white hover:border-zinc-700'
                                }`}
                        >
                            {type}
                        </button>
                    ))}
                </div>
            </div>

            {/* Stats Summary */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                {[
                    { label: 'Total In', value: '$18,000.00', color: 'text-green-400' },
                    { label: 'Total Out', value: '$1,089.99', color: 'text-red-500' },
                    { label: 'Pending', value: '1', color: 'text-yellow-500' },
                    { label: 'Failed', value: '1', color: 'text-red-500' },
                ].map((stat) => (
                    <div key={stat.label} className="bg-zinc-900 border-4 border-zinc-800 p-4">
                        <p className="text-zinc-500 font-bold uppercase text-xs tracking-widest">{stat.label}</p>
                        <p className={`font-black text-xl mt-1 ${stat.color}`}>{stat.value}</p>
                    </div>
                ))}
            </div>

            {/* Transaction List */}
            <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
                <CardHeader className="border-b-4 border-zinc-800 pb-4">
                    <CardTitle className="text-lg text-white flex items-center justify-between">
                        <span className="flex items-center gap-2">
                            <Filter size={18} className="text-ochre-800" />
                            {filtered.length} Transaction{filtered.length !== 1 ? 's' : ''}
                        </span>
                    </CardTitle>
                </CardHeader>
                <CardContent className="pt-0">
                    {filtered.length === 0 ? (
                        <div className="py-12 text-center text-zinc-600">
                            <p className="font-bold uppercase tracking-widest">No transactions found</p>
                        </div>
                    ) : (
                        <div className="divide-y-4 divide-zinc-800">
                            {filtered.map((tx) => (
                                <TransactionRow key={tx.transactionId} transaction={tx} />
                            ))}
                        </div>
                    )}
                </CardContent>
            </Card>
        </>
    );
}

function TransactionRow({ transaction }: { transaction: Transaction }) {
    const isPositive = transaction.transactionType === 'CREDIT';
    const amountColor = isPositive ? 'text-green-400' : 'text-red-500';
    const amountPrefix = isPositive ? '+' : '-';

    const renderStatusIcon = () => {
        switch (transaction.status) {
            case 'SUCCESS': return <CheckCircle2 size={16} className="text-zinc-500" />;
            case 'PENDING': return <Clock size={16} className="text-yellow-500" />;
            case 'FAILED': return <XCircle size={16} className="text-red-500" />;
        }
    };

    return (
        <div className="py-5 flex items-center justify-between group hover:bg-zinc-800/30 transition-colors px-2">
            <div className="flex items-center gap-4">
                <div className={`p-3 border-2 border-zinc-800 bg-zinc-950 ${isPositive ? 'text-green-400' : 'text-red-500'}`}>
                    {isPositive ? <ArrowDownRight size={22} /> : <ArrowUpRight size={22} />}
                </div>
                <div>
                    <div className="flex items-center gap-2">
                        <h4 className="font-bold text-base">{transaction.transactionType}</h4>
                        <span className={`text-xs font-bold uppercase px-2 py-0.5 border-2 ${transaction.status === 'SUCCESS' ? 'border-zinc-700 text-zinc-400' :
                                transaction.status === 'PENDING' ? 'border-yellow-500/30 text-yellow-500' :
                                    'border-red-500/30 text-red-500'
                            }`}>
                            {renderStatusIcon()}
                        </span>
                    </div>
                    <div className="flex items-center gap-2 text-xs text-zinc-500 font-mono tracking-wider mt-1">
                        <span>{new Date(transaction.timeOfCreation).toLocaleDateString()}</span>
                        <span>•</span>
                        <span>{new Date(transaction.timeOfCreation).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                        <span>•</span>
                        <span className="truncate max-w-[120px] md:max-w-none">{transaction.transactionId}</span>
                    </div>
                </div>
            </div>
            <div className="text-right">
                <div className={`font-black text-xl ${amountColor}`}>
                    {amountPrefix}${transaction.amount.toFixed(2)}
                </div>
                <p className="text-zinc-600 font-mono text-xs mt-1">{transaction.accountNumber}</p>
            </div>
        </div>
    );
}
