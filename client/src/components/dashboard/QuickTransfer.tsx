import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../ui/Card';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Send } from 'lucide-react';

export function QuickTransfer() {
    const [receiver, setReceiver] = useState('');
    const [amount, setAmount] = useState('');

    const handleTransfer = (e: React.FormEvent) => {
        e.preventDefault();
        // Construct the TransferRequest mock
        const transferRequest = {
            sender: "CURRENT_USER_ACCT", // Mocked
            receiver,
            amount: parseFloat(amount)
        };
        console.log("Mock Transfer Request:", transferRequest);
        // Reset form
        setReceiver('');
        setAmount('');
    };

    return (
        <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
            <CardHeader className="border-b-4 border-zinc-800 pb-4">
                <CardTitle className="text-xl md:text-2xl text-white">Quick Transfer</CardTitle>
            </CardHeader>
            <CardContent className="pt-6">
                <form onSubmit={handleTransfer} className="space-y-4">
                    <div className="space-y-2">
                        <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">Receiver Account</label>
                        <Input
                            type="text"
                            placeholder="0000000000"
                            value={receiver}
                            onChange={(e) => setReceiver(e.target.value)}
                            className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono text-lg"
                            required
                        />
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
                        className="w-full bg-ochre-800 hover:bg-ochre-800/90 text-white uppercase font-black tracking-widest py-6 border-4 border-black group"
                    >
                        <span className="flex items-center justify-center gap-2">
                            Send Money
                            <Send size={20} className="group-hover:translate-x-1 transition-transform" />
                        </span>
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
}
