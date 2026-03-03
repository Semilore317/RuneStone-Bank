import { LayoutDashboard, Send, History, Settings, LogOut } from 'lucide-react';
import React from 'react';
import { TotalBalance } from './dashboard/TotalBalance';
import { QuickTransfer } from './dashboard/QuickTransfer';

interface DashboardProps {
    children?: React.ReactNode;
}

export function Dashboard({ children }: DashboardProps) {
    return (
        <div className="min-h-screen bg-zinc-950 text-white flex select-none">
            {/* Sidebar */}
            <aside className="w-20 md:w-64 bg-zinc-950 border-r-4 border-zinc-800 flex flex-col justify-between py-8 shrink-0">
                <div>
                    <div className="px-4 md:px-8 mb-12">
                        <h1 className="text-2xl md:text-3xl font-black uppercase tracking-tighter text-white hidden md:block">
                            RuneStone
                        </h1>
                        <h1 className="text-2xl font-black uppercase tracking-tighter text-white md:hidden text-center">
                            RS
                        </h1>
                    </div>

                    <nav className="flex flex-col space-y-2 px-2 md:px-6">
                        <NavItem icon={<LayoutDashboard size={24} />} label="Dashboard" active />
                        <NavItem icon={<Send size={24} />} label="Transfer" />
                        <NavItem icon={<History size={24} />} label="Transactions" />
                    </nav>
                </div>

                <div className="px-2 md:px-6 flex flex-col space-y-2">
                    <NavItem icon={<Settings size={24} />} label="Settings" />
                    <NavItem icon={<LogOut size={24} />} label="Logout" />
                </div>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 p-6 md:p-12 overflow-y-auto">
                <div className="max-w-6xl mx-auto space-y-8 relative z-10">
                    <header className="mb-12">
                        <h2 className="text-4xl md:text-5xl font-black uppercase tracking-tighter">Overview</h2>
                    </header>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {/* Total Balance spans across on larger screens if needed, but let's keep it responsive */}
                        <div className="md:col-span-2 lg:col-span-3">
                            <TotalBalance />
                        </div>

                        {/* Quick Transfer */}
                        <div className="md:col-span-1 lg:col-span-1">
                            <QuickTransfer />
                        </div>

                        {/* Recent Transactions Placeholder */}
                        <div className="md:col-span-1 lg:col-span-2">
                            {children}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}

function NavItem({ icon, label, active = false }: { icon: React.ReactNode; label: string; active?: boolean }) {
    return (
        <button
            className={`flex items-center space-x-4 p-4 uppercase font-bold text-lg md:text-xl border-4 border-transparent transition-all group
            ${active
                    ? 'bg-zinc-900 border-l-ochre-800 text-white'
                    : 'text-zinc-500 hover:bg-zinc-900 hover:text-white hover:border-l-zinc-700'
                }`}
        >
            <span className={`${active ? 'text-ochre-800' : 'group-hover:text-white'}`}>
                {icon}
            </span>
            <span className="hidden md:block">
                {label}
            </span>
        </button>
    );
}
