import { useState, useEffect } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Send, UserSearch, CheckCircle2, AlertCircle, RefreshCw } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { lookupAccountName, transferFunds, fetchRecentTransactions } from '../services/dashboard';

export function TransferPage() {
    const { user } = useAuth();
    const [receiver, setReceiver] = useState('');
    const [amount, setAmount] = useState('');
    const [resolvedName, setResolvedName] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [error, setError] = useState('');
    const [isLookingUp, setIsLookingUp] = useState(false);
    const [isTransferring, setIsTransferring] = useState(false);

    // Recent recipients state
    const [recentRecipients, setRecentRecipients] = useState<{ name: string, account: string, lastAmount: number }[]>([]);
    const [isLoadingRecipients, setIsLoadingRecipients] = useState(false);

    const loadRecentRecipients = async () => {
        if (!user?.accountNumber) return;
        setIsLoadingRecipients(true);
        try {
            const data = await fetchRecentTransactions(user.accountNumber);

            // Filter out outgoing transfers that have a counterparty
            const outgoingTransfers = data.filter(tx => tx.transactionType === 'TRANSFER' && tx.counterpartyAccountNumber && tx.counterpartyName);

            // Build a unique list of recipients
            const uniqueRecipients = new Map<string, { name: string, account: string, lastAmount: number }>();

            outgoingTransfers.forEach(tx => {
                if (tx.counterpartyAccountNumber && !uniqueRecipients.has(tx.counterpartyAccountNumber)) {
                    uniqueRecipients.set(tx.counterpartyAccountNumber, {
                        name: tx.counterpartyName || 'Unknown',
                        account: tx.counterpartyAccountNumber,
                        lastAmount: tx.amount
                    });
                }
            });

            setRecentRecipients(Array.from(uniqueRecipients.values()).slice(0, 5));
        } catch (error) {
            console.error("Failed to load recipients");
        } finally {
            setIsLoadingRecipients(false);
        }
    };

    useEffect(() => {
        loadRecentRecipients();
    }, [user?.accountNumber]);

    const handleLookup = async () => {
        if (receiver.length < 5) {
            setError('Please enter a valid account number.');
            return;
        }

        if (receiver === user?.accountNumber) {
            setError('You cannot transfer to yourself.');
            return;
        }

        setIsLookingUp(true);
        setError('');
        setResolvedName(null);

        try {
            const response = await lookupAccountName(receiver);
            // "004" corresponds to ACCOUNT_FOUND_CODE in AccountUtils.java
            if (response.responseCode === "004" && response.accountInfo) {
                setResolvedName(response.accountInfo.accountName);
            } else {
                setError(response.responseMessage || 'Account not found.');
            }
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : 'Error looking up account.');
        } finally {
            setIsLookingUp(false);
        }
    };

    const handleTransfer = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!resolvedName || !user?.accountNumber) return;

        setIsTransferring(true);
        setError('');
        setSuccessMessage('');

        try {
            await transferFunds({
                sender: user.accountNumber,
                receiver,
                amount: parseFloat(amount)
            });

            setSuccessMessage(`$${parseFloat(amount).toFixed(2)} sent to ${resolvedName} successfully.`);
            setReceiver('');
            setAmount('');
            setResolvedName(null);
            setTimeout(() => setSuccessMessage(''), 5000);

            // Reload recent recipients
            loadRecentRecipients();
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : 'Transfer failed.');
        } finally {
            setIsTransferring(false);
        }
    };

    return (
        <>
            <header className="mb-12">
                <h2 className="text-4xl md:text-5xl font-black uppercase tracking-tighter">Transfer</h2>
                <p className="text-zinc-500 font-bold uppercase tracking-widest text-sm mt-2">Send money to another account</p>
            </header>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Transfer Form */}
                <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
                    <CardHeader className="border-b-4 border-zinc-800 pb-4">
                        <CardTitle className="text-xl md:text-2xl text-white flex items-center gap-3">
                            <Send size={22} className="text-ochre-800" />
                            New Transfer
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="pt-6">
                        {successMessage && (
                            <div className="mb-6 p-4 bg-green-500/10 border-4 border-green-500/30 flex items-start gap-3">
                                <CheckCircle2 size={20} className="text-green-500 shrink-0 mt-0.5" />
                                <p className="text-green-400 font-bold text-sm uppercase tracking-wider">{successMessage}</p>
                            </div>
                        )}

                        {error && (
                            <div className="mb-6 p-4 bg-red-500/10 border-4 border-red-500/30 flex items-start gap-3">
                                <AlertCircle size={20} className="text-red-500 shrink-0 mt-0.5" />
                                <p className="text-red-400 font-bold text-sm uppercase tracking-wider">{error}</p>
                            </div>
                        )}

                        <form onSubmit={handleTransfer} className="space-y-5">
                            <div className="space-y-2">
                                <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">Receiver Account</label>
                                <div className="flex gap-2">
                                    <Input
                                        type="text"
                                        placeholder="0000000000"
                                        value={receiver}
                                        onChange={(e) => { setReceiver(e.target.value); setResolvedName(null); setError(''); }}
                                        className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono text-lg flex-1"
                                        maxLength={10}
                                        required
                                    />
                                    <Button
                                        type="button"
                                        onClick={handleLookup}
                                        disabled={isLookingUp}
                                        className="bg-zinc-800 hover:bg-zinc-700 text-white border-4 border-zinc-700 shrink-0 disabled:opacity-50"
                                    >
                                        {isLookingUp ? <RefreshCw size={20} className="animate-spin" /> : <UserSearch size={20} />}
                                    </Button>
                                </div>
                                {resolvedName && (
                                    <p className="text-green-400 font-bold text-sm flex items-center gap-2 mt-1 uppercase">
                                        <CheckCircle2 size={14} />
                                        {resolvedName}
                                    </p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">Amount USD</label>
                                <div className="relative">
                                    <span className="absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500 font-black text-xl">$</span>
                                    <Input
                                        type="number"
                                        placeholder="0.00"
                                        value={amount}
                                        onChange={(e) => setAmount(e.target.value)}
                                        min="0.01"
                                        step="0.01"
                                        className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-black text-xl pl-10"
                                        required
                                    />
                                </div>
                            </div>

                            <Button
                                type="submit"
                                disabled={!resolvedName || isTransferring}
                                className="w-full bg-ochre-800 hover:bg-ochre-800/90 text-white uppercase font-black tracking-widest py-6 border-4 border-black group disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <span className="flex items-center justify-center gap-2">
                                    {isTransferring ? 'Processing...' : 'Send Money'}
                                    {!isTransferring && <Send size={20} className="group-hover:translate-x-1 transition-transform" />}
                                </span>
                            </Button>
                        </form>
                    </CardContent>
                </Card>

                {/* Recent Recipients */}
                <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
                    <CardHeader className="border-b-4 border-zinc-800 pb-4">
                        <CardTitle className="text-xl md:text-2xl text-white">Recent Recipients</CardTitle>
                    </CardHeader>
                    <CardContent className="pt-0">
                        {isLoadingRecipients ? (
                            <div className="py-8 text-center text-zinc-500 font-bold uppercase tracking-widest text-sm">
                                Loading...
                            </div>
                        ) : recentRecipients.length === 0 ? (
                            <div className="py-8 text-center text-zinc-500 font-bold uppercase tracking-widest text-sm">
                                No recent recipients found
                            </div>
                        ) : (
                            <div className="divide-y-4 divide-zinc-800">
                                {recentRecipients.map((recipient) => (
                                    <button
                                        key={recipient.account}
                                        onClick={() => { setReceiver(recipient.account); setResolvedName(recipient.name); setError(''); }}
                                        className="w-full py-5 flex items-center justify-between hover:bg-zinc-800/50 transition-colors px-2 group"
                                    >
                                        <div className="text-left">
                                            <p className="font-bold text-base">{recipient.name}</p>
                                            <p className="text-zinc-500 font-mono text-sm mt-1">{recipient.account}</p>
                                        </div>
                                        <div className="text-right">
                                            <p className="text-zinc-400 font-bold text-sm">Last sent</p>
                                            <p className="font-black text-lg">${recipient.lastAmount.toFixed(2)}</p>
                                        </div>
                                    </button>
                                ))}
                            </div>
                        )}
                    </CardContent>
                </Card>
            </div>
        </>
    );
}
