import { TotalBalance } from '../components/dashboard/TotalBalance';
import { QuickTransfer } from '../components/dashboard/QuickTransfer';
import { RecentTransactions } from '../components/dashboard/RecentTransactions';

export function DashboardPage() {
    return (
        <>
            <header className="mb-12">
                <h2 className="text-4xl md:text-5xl font-black uppercase tracking-tighter">DashBoard</h2>
            </header>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <div className="md:col-span-2 lg:col-span-3">
                    <TotalBalance />
                </div>

                <div className="md:col-span-1 lg:col-span-1">
                    <QuickTransfer />
                </div>

                <div className="md:col-span-1 lg:col-span-2">
                    <RecentTransactions />
                </div>
            </div>
        </>
    );
}
