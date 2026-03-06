import { createContext, useContext, useState, useCallback, useEffect, type ReactNode } from 'react';
import {
    loginUser,
    logoutUser,
    getToken,
    getStoredAccountInfo,
    type AccountInfo,
} from '../services/auth';

interface AuthContextType {
    user: AccountInfo | null;
    token: string | null;
    isAuthenticated: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<AccountInfo | null>(null);
    const [token, setToken] = useState<string | null>(null);

    // Hydrate from localStorage on mount
    useEffect(() => {
        const storedToken = getToken();
        const storedUser = getStoredAccountInfo();
        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(storedUser);
        }
    }, []);

    const login = useCallback(async (email: string, password: string) => {
        const response = await loginUser(email, password);
        setToken(response.jwt);
        setUser(response.accountInfo);
    }, []);

    const logout = useCallback(() => {
        logoutUser();
        setToken(null);
        setUser(null);
    }, []);

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                isAuthenticated: !!token,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
