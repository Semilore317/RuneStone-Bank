import { LayoutDashboard, Send, History, Settings, LogOut } from 'lucide-react';
import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import logo from '../assets/runestone.png';

export function AppLayout() {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login', { replace: true });
    };

    return (
        <div className="h-screen bg-zinc-950 text-white flex select-none overflow-hidden">
            {/* Sidebar */}
            <aside className="w-20 md:w-64 h-full bg-zinc-950 border-r-4 border-zinc-800 flex flex-col justify-between py-8 shrink-0 overflow-y-auto custom-scrollbar">
                <div>
                    <div className="px-4 md:px-8 mb-12 flex justify-center">
                        <div className="relative h-16 w-16 md:h-24 md:w-24 rounded-[1.5rem] md:rounded-[2rem] overflow-hidden shadow-brutal border-2 border-zinc-800 shrink-0 bg-zinc-900">
                            <img
                                src={logo}
                                alt="RuneStone Logo"
                                className="absolute inset-0 h-full w-full object-cover scale-110"
                            />
                        </div>
                    </div>

                    <nav className="flex flex-col space-y-2 px-2 md:px-6">
                        <SidebarLink to="/dashboard" icon={<LayoutDashboard size={24} />} label="Dashboard" />
                        <SidebarLink to="/transfer" icon={<Send size={24} />} label="Transfer" />
                        <SidebarLink to="/transactions" icon={<History size={24} />} label="Transactions" />
                    </nav>
                </div>

                <div className="px-2 md:px-6 flex flex-col space-y-2">
                    <SidebarLink to="/settings" icon={<Settings size={24} />} label="Settings" />
                    <button
                        onClick={handleLogout}
                        className="flex items-center space-x-4 p-4 uppercase font-bold text-lg md:text-xl border-4 border-transparent transition-all group text-zinc-500 hover:bg-zinc-900 hover:text-white hover:border-l-zinc-700"
                    >
                        <span className="group-hover:text-white">
                            <LogOut size={24} />
                        </span>
                        <span className="hidden md:block">Logout</span>
                    </button>
                </div>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 p-6 md:p-12 overflow-y-auto">
                <div className="max-w-6xl mx-auto space-y-8 relative z-10">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}

function SidebarLink({ to, icon, label }: { to: string; icon: React.ReactNode; label: string }) {
    return (
        <NavLink
            to={to}
            className={({ isActive }) =>
                `flex items-center space-x-4 p-4 uppercase font-bold text-lg md:text-xl border-4 border-transparent transition-all group
                ${isActive
                    ? 'bg-zinc-900 border-l-ochre-800 text-white'
                    : 'text-zinc-500 hover:bg-zinc-900 hover:text-white hover:border-l-zinc-700'
                }`
            }
        >
            {({ isActive }) => (
                <>
                    <span className={isActive ? 'text-ochre-800' : 'group-hover:text-white'}>
                        {icon}
                    </span>
                    <span className="hidden md:block">{label}</span>
                </>
            )}
        </NavLink>
    );
}
