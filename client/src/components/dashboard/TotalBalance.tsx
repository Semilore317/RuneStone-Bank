import { Card, CardContent, CardHeader, CardTitle } from '../ui/Card';
import { TrendingUp, TrendingDown } from 'lucide-react';

interface AccountInfo {
    accountName: string;
    accountBalance: number;
    accountNumber: string;
}

export function TotalBalance() {
    // Mocking the AccountInfo expected from the backend
    const mockBalance: AccountInfo = {
        accountName: "John Doe",
        accountBalance: 125430.25,
        accountNumber: "1234567890"
    };

    const isPositive = mockBalance.accountBalance >= 0;

    return (
        <Card className="border-t-8 border-t-ochre-800 bg-zinc-900 border-x-4 border-b-4 border-x-zinc-800 border-b-zinc-800 text-white relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-ochre-800 opacity-10 rounded-full -translate-y-12 translate-x-12 blur-2xl" />

            <CardHeader className="border-b-4 border-zinc-800 pb-2">
                <CardTitle className="text-zinc-400 text-sm md:text-base flex items-center justify-between">
                    <span>Total Balance</span>
                    {isPositive ? (
                        <TrendingUp size={20} className="text-green-500" />
                    ) : (
                        <TrendingDown size={20} className="text-red-500" />
                    )}
                </CardTitle>
            </CardHeader>
            <CardContent className="pt-6">
                <div className="text-4xl md:text-6xl font-black break-words tracking-tighter">
                    ${mockBalance.accountBalance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </div>
                <div className="mt-4 flex flex-col md:flex-row justify-between items-start md:items-center text-sm md:text-base font-bold text-zinc-500 uppercase tracking-widest gap-2">
                    <span>{mockBalance.accountName}</span>
                    <span className="bg-zinc-950 px-3 py-1 border-2 border-zinc-800">{mockBalance.accountNumber}</span>
                </div>
            </CardContent>
        </Card>
    );
}
