import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/Card';
import { Copy, Check, Eye, EyeOff } from 'lucide-react';

interface AccountInfo {
    accountName: string;
    accountBalance: number;
    accountNumber: string;
}

export function TotalBalance() {
    const [isRevealed, setIsRevealed] = useState(false);
    const [copied, setCopied] = useState(false);

    // Mocking the AccountInfo expected from the backend
    const mockBalance: AccountInfo = {
        accountName: "John Doe",
        accountBalance: 125430.25,
        accountNumber: "1234567890"
    };

    const handleCopy = () => {
        navigator.clipboard.writeText(mockBalance.accountNumber);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
    };

    return (
        <Card className="border-t-8 border-t-ochre-800 bg-zinc-900 border-x-4 border-b-4 border-x-zinc-800 border-b-zinc-800 text-white relative overflow-hidden group/card">
            <div className="absolute top-0 right-0 w-32 h-32 bg-ochre-800 opacity-10 rounded-full -translate-y-12 translate-x-12 blur-2xl transition-opacity duration-1000 group-hover/card:opacity-20" />

            <CardHeader className="border-b-4 border-zinc-800 pb-2">
                <CardTitle className="text-zinc-400 text-sm md:text-base flex items-center justify-between">
                    <span>Total Balance</span>
                    <button
                        onClick={() => setIsRevealed(!isRevealed)}
                        className="p-1 hover:text-white transition-colors"
                        title={isRevealed ? "Hide balance" : "Show balance"}
                    >
                        {isRevealed ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                </CardTitle>
            </CardHeader>
            <CardContent className="pt-6">
                <div className={`text-4xl md:text-6xl font-black break-words tracking-tighter transition-all duration-300 ${!isRevealed ? "blur-md select-none opacity-50" : ""}`}>
                    ${mockBalance.accountBalance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </div>
                <div className="mt-4 flex flex-col md:flex-row justify-between items-start md:items-center text-sm md:text-base font-bold text-zinc-500 uppercase tracking-widest gap-2">
                    <span>{mockBalance.accountName}</span>
                    <button
                        onClick={handleCopy}
                        className="bg-zinc-950 px-3 py-1 border-2 border-zinc-800 hover:border-zinc-600 hover:text-zinc-300 transition-colors flex items-center gap-2"
                        title="Copy Account Number"
                    >
                        <span>{mockBalance.accountNumber}</span>
                        {copied ? <Check size={16} className="text-green-500" /> : <Copy size={16} />}
                    </button>
                </div>
            </CardContent>
        </Card>
    );
}
