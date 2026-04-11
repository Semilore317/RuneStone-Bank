import { createContext, useContext, useState, useCallback, useEffect, useRef, type ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    loginUser,
    logoutUser,
    getToken,
    getStoredAccountInfo,
    isTokenExpired,
    getTimeUntilExpiry,
    type AccountInfo,
} from '../services/auth';

interface AuthContextType {
    user: AccountInfo | null;
    token: string | null;
    isAuthenticated: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextType | null>(null);

/** Compute initial auth state synchronously from localStorage */
function getInitialAuthState(): { token: string | null; user: AccountInfo | null } {
    const storedToken = getToken();
    const storedUser = getStoredAccountInfo();

    if (storedToken && storedUser && !isTokenExpired()) {
        return { token: storedToken, user: storedUser };
    }

    // Expired or missing — clean up
    if (storedToken) {
        logoutUser();
    }
    return { token: null, user: null };
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<AccountInfo | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();
    const expiryTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    const clearExpiryTimer = useCallback(() => {
        if (expiryTimerRef.current) {
            clearTimeout(expiryTimerRef.current);
            expiryTimerRef.current = null;
        }
    }, []);

    const performLogout = useCallback(() => {
        clearExpiryTimer();
        logoutUser();
        setToken(null);
        setUser(null);
        navigate('/login', { replace: true });
    }, [clearExpiryTimer, navigate]);

    const startExpiryTimer = useCallback(() => {
        clearExpiryTimer();
        const remaining = getTimeUntilExpiry();

        if (remaining <= 0) {
            performLogout();
            return;
        }

        expiryTimerRef.current = setTimeout(performLogout, remaining);
    }, [clearExpiryTimer, performLogout]);

    useEffect(() => {
        const initial = getInitialAuthState();
        if (initial.token) {
            setToken(initial.token);
            setUser(initial.user);
            startExpiryTimer();
        }
        setIsLoading(false);
        return () => clearExpiryTimer();
    }, [clearExpiryTimer, startExpiryTimer]);

    const login = useCallback(async (email: string, password: string) => {
        const response = await loginUser(email, password);
        setToken(response.jwt);
        setUser(response.accountInfo);
        startExpiryTimer();
    }, [startExpiryTimer]);

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                isAuthenticated: !!token,
                login,
                logout: performLogout,
            }}
        >
            {!isLoading && children}
        </AuthContext.Provider>
    );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
