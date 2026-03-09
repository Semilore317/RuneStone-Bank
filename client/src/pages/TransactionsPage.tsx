import { useState, useEffect } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { ArrowUpRight, ArrowDownRight, Clock, XCircle, CheckCircle2, Search, Filter } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { fetchRecentTransactions, parseTransactionDate, type Transaction } from '../services/dashboard';

type FilterType = 'ALL' | 'CREDIT' | 'DEBIT' | 'TRANSFER';

export function TransactionsPage() {
    const { user } = useAuth();
    const [searchTerm, setSearchTerm] = useState('');
    const [filterType, setFilterType] = useState<FilterType>('ALL');
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        const load = async () => {
            if (!user?.accountNumber) return;
            setIsLoading(true);
            try {
                const data = await fetchRecentTransactions(user.accountNumber);
                setTransactions(data);
            } catch (error) {
                console.error(error);
            } finally {
                setIsLoading(false);
            }
        };
        load();
    }, [user?.accountNumber]);

    const filtered = transactions.filter((tx) => {
        const matchesSearch = tx.transactionId.toLowerCase().includes(searchTerm.toLowerCase()) ||
            tx.accountNumber.includes(searchTerm) ||
            (tx.counterpartyName?.toLowerCase() || '').includes(searchTerm.toLowerCase());
        const matchesFilter = filterType === 'ALL' || tx.transactionType === filterType;
        return matchesSearch && matchesFilter;
    });

    const totalIn = transactions.filter(t => t.transactionType === 'CREDIT' && t.status === 'SUCCESS').reduce((s, t) => s + t.amount, 0);
    const totalOut = transactions.filter(t => (t.transactionType === 'DEBIT' || t.transactionType === 'TRANSFER') && t.status === 'SUCCESS').reduce((s, t) => s + t.amount, 0);
    const pendingCount = transactions.filter(t => t.status === 'PENDING').length;
    const failedCount = transactions.filter(t => t.status === 'FAILED').length;

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
                <div className="flex gap-2" role="group" aria-label="Filter transactions">
                    {(['ALL', 'CREDIT', 'DEBIT', 'TRANSFER'] as FilterType[]).map((type) => (
                        <button
                            key={type}
                            onClick={() => setFilterType(type)}
                            aria-pressed={filterType === type}
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
                    { label: 'Total In', value: `$${totalIn.toFixed(2)}`, color: 'text-green-400' },
                    { label: 'Total Out', value: `$${totalOut.toFixed(2)}`, color: 'text-red-500' },
                    { label: 'Pending', value: pendingCount.toString(), color: 'text-yellow-500' },
                    { label: 'Failed', value: failedCount.toString(), color: 'text-red-500' },
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
                    {isLoading ? (
                        <div className="py-12 text-center text-zinc-600">
                            <p className="font-bold uppercase tracking-widest">Loading...</p>
                        </div>
                    ) : filtered.length === 0 ? (
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

    const parsedDate = parseTransactionDate(transaction.timeOfCreation);

    return (
        <div className="py-5 flex items-center justify-between group hover:bg-zinc-800/30 transition-colors px-2">
            <div className="flex items-center gap-4">
                <div className={`p-3 border-2 border-zinc-800 bg-zinc-950 ${isPositive ? 'text-green-400' : 'text-red-500'}`}>
                    {isPositive ? <ArrowDownRight size={22} /> : <ArrowUpRight size={22} />}
                </div>
                <div>
                    <div className="flex items-center gap-2">
                        <h4 className="font-bold text-base">
                            {transaction.transactionType === 'TRANSFER' && transaction.counterpartyName
                                ? `Transfer to ${transaction.counterpartyName}`
                                : transaction.transactionType === 'CREDIT' && transaction.counterpartyName
                                    ? `Transfer from ${transaction.counterpartyName}`
                                    : transaction.transactionType}
                        </h4>
                        <span className={`text-xs font-bold uppercase px-2 py-0.5 border-2 ${transaction.status === 'SUCCESS' ? 'border-zinc-700 text-zinc-400' :
                            transaction.status === 'PENDING' ? 'border-yellow-500/30 text-yellow-500' :
                                'border-red-500/30 text-red-500'
                            }`}>
                            {renderStatusIcon()}
                        </span>
                    </div>
                    <div className="flex items-center gap-2 text-xs text-zinc-500 font-mono tracking-wider mt-1">
                        <span>{parsedDate.toLocaleDateString()}</span>
                        <span>•</span>
                        <span>{parsedDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                        <span>•</span>
                        <span className="truncate max-w-[120px] md:max-w-none">{transaction.transactionId}</span>
                    </div>
                </div>
            </div>
            <div className="text-right">
                <div className={`font-black text-xl ${amountColor}`}>
                    {amountPrefix}${transaction.amount.toFixed(2)}
                </div>
                <p className="text-zinc-600 font-mono text-xs mt-1">{transaction.counterpartyAccountNumber || transaction.accountNumber}</p>
            </div>
        </div>
    );
}
