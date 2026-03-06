import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { User, Bell, Shield, Save, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export function SettingsPage() {
    const { user } = useAuth();
    const [saved, setSaved] = useState(false);

    // Mock settings state
    const [firstName, setFirstName] = useState(user?.accountName?.split(' ')[0] || 'John');
    const [lastName, setLastName] = useState(user?.accountName?.split(' ').slice(1).join(' ') || 'Doe');
    const [email, setEmail] = useState('john.doe@example.com');
    const [emailNotifs, setEmailNotifs] = useState(true);
    const [loginAlerts, setLoginAlerts] = useState(true);
    const [transferAlerts, setTransferAlerts] = useState(false);

    const handleSave = (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Settings saved:', { firstName, lastName, email });
        setSaved(true);
        setTimeout(() => setSaved(false), 3000);
    };

    return (
        <>
            <header className="mb-12">
                <h2 className="text-4xl md:text-5xl font-black uppercase tracking-tighter">Settings</h2>
                <p className="text-zinc-500 font-bold uppercase tracking-widest text-sm mt-2">Manage your account</p>
            </header>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Profile Settings */}
                <div className="lg:col-span-2 space-y-8">
                    <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
                        <CardHeader className="border-b-4 border-zinc-800 pb-4">
                            <CardTitle className="text-xl md:text-2xl text-white flex items-center gap-3">
                                <User size={22} className="text-ochre-800" />
                                Profile
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="pt-6">
                            {saved && (
                                <div className="mb-6 p-4 bg-green-500/10 border-4 border-green-500/30 flex items-start gap-3">
                                    <CheckCircle2 size={20} className="text-green-500 shrink-0 mt-0.5" />
                                    <p className="text-green-400 font-bold text-sm uppercase tracking-wider">Settings saved successfully</p>
                                </div>
                            )}

                            <form onSubmit={handleSave} className="space-y-5">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div className="space-y-2">
                                        <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">First Name</label>
                                        <Input
                                            type="text"
                                            value={firstName}
                                            onChange={(e) => setFirstName(e.target.value)}
                                            className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">Last Name</label>
                                        <Input
                                            type="text"
                                            value={lastName}
                                            onChange={(e) => setLastName(e.target.value)}
                                            className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono"
                                        />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">Email Address</label>
                                    <Input
                                        type="email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono"
                                    />
                                </div>

                                <Button
                                    type="submit"
                                    className="bg-ochre-800 hover:bg-ochre-800/90 text-white uppercase font-black tracking-widest py-4 border-4 border-black group"
                                >
                                    <span className="flex items-center gap-2">
                                        <Save size={18} />
                                        Save Changes
                                    </span>
                                </Button>
                            </form>
                        </CardContent>
                    </Card>

                    {/* Security */}
                    <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
                        <CardHeader className="border-b-4 border-zinc-800 pb-4">
                            <CardTitle className="text-xl md:text-2xl text-white flex items-center gap-3">
                                <Shield size={22} className="text-ochre-800" />
                                Security
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="pt-6 space-y-5">
                            <div className="space-y-2">
                                <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">Current Password</label>
                                <Input
                                    type="password"
                                    placeholder="••••••••"
                                    className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono"
                                />
                            </div>
                            <div className="space-y-2">
                                <label className="text-xs md:text-sm font-bold uppercase tracking-widest text-zinc-500">New Password</label>
                                <Input
                                    type="password"
                                    placeholder="••••••••"
                                    className="bg-zinc-950 border-4 border-zinc-800 text-white placeholder-zinc-700 font-mono"
                                />
                            </div>
                            <Button
                                type="button"
                                className="bg-zinc-800 hover:bg-zinc-700 text-white uppercase font-black tracking-widest py-4 border-4 border-zinc-700"
                            >
                                Update Password
                            </Button>
                        </CardContent>
                    </Card>
                </div>

                {/* Notification Preferences */}
                <div>
                    <Card className="bg-zinc-900 border-4 border-zinc-800 text-white">
                        <CardHeader className="border-b-4 border-zinc-800 pb-4">
                            <CardTitle className="text-xl md:text-2xl text-white flex items-center gap-3">
                                <Bell size={22} className="text-ochre-800" />
                                Notifications
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="pt-6">
                            <div className="space-y-4">
                                <ToggleItem
                                    label="Email Notifications"
                                    description="Receive updates via email"
                                    checked={emailNotifs}
                                    onChange={setEmailNotifs}
                                />
                                <ToggleItem
                                    label="Login Alerts"
                                    description="Alert on new sign-ins"
                                    checked={loginAlerts}
                                    onChange={setLoginAlerts}
                                />
                                <ToggleItem
                                    label="Transfer Alerts"
                                    description="Alert on money transfers"
                                    checked={transferAlerts}
                                    onChange={setTransferAlerts}
                                />
                            </div>
                        </CardContent>
                    </Card>

                    {/* Account Info */}
                    <Card className="bg-zinc-900 border-4 border-zinc-800 text-white mt-8">
                        <CardHeader className="border-b-4 border-zinc-800 pb-4">
                            <CardTitle className="text-lg text-white">Account Info</CardTitle>
                        </CardHeader>
                        <CardContent className="pt-4 space-y-3">
                            <InfoRow label="Account Number" value={user?.accountNumber || '1234567890'} />
                            <InfoRow label="Account Name" value={user?.accountName || 'John Doe'} />
                            <InfoRow label="Member Since" value="Feb 2026" />
                        </CardContent>
                    </Card>
                </div>
            </div>
        </>
    );
}

function ToggleItem({ label, description, checked, onChange }: {
    label: string; description: string; checked: boolean; onChange: (v: boolean) => void;
}) {
    return (
        <button
            onClick={() => onChange(!checked)}
            className="w-full flex items-center justify-between p-4 border-4 border-zinc-800 hover:border-zinc-700 transition-colors"
        >
            <div className="text-left">
                <p className="font-bold text-sm uppercase tracking-wider">{label}</p>
                <p className="text-zinc-500 text-xs mt-0.5">{description}</p>
            </div>
            <div className={`w-12 h-7 rounded-none border-2 transition-colors relative ${checked ? 'bg-ochre-800 border-ochre-800' : 'bg-zinc-800 border-zinc-700'}`}>
                <div className={`absolute top-0.5 w-5 h-5 bg-white transition-transform ${checked ? 'translate-x-5' : 'translate-x-0.5'}`} />
            </div>
        </button>
    );
}

function InfoRow({ label, value }: { label: string; value: string }) {
    return (
        <div className="flex justify-between items-center py-2 border-b border-zinc-800 last:border-0">
            <span className="text-zinc-500 text-xs font-bold uppercase tracking-widest">{label}</span>
            <span className="font-mono text-sm font-bold">{value}</span>
        </div>
    );
}
