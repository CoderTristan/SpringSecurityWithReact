import './App.css'
import React, { useState } from 'react';
import { AuthProvider, useAuth } from './components/AuthContext';
import { Login } from './components/Login';
import { Register } from './components/Register';
import { apiRequest } from './utils/api';

const Dashboard: React.FC = () => {
  const { logout } = useAuth();

  const handleTestRoute = async () => {
    try {
      const response = await apiRequest<{ message: string }>('/demo-controller-endpoint', 'GET');
      alert(`Backend handshake successful!`);
      console.log(response);
    } catch (error) {
      alert(error instanceof Error ? error.message : 'Handshake failed');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto bg-white rounded-2xl shadow-md p-8 border border-gray-200">
        <div className="flex flex-col sm:flex-row items-center justify-between border-b border-gray-200 pb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Application Workspace</h1>
            <p className="text-sm text-gray-500 mt-1">State: Connection Secure (JWT Valid)</p>
          </div>
          <button
            onClick={logout}
            className="mt-4 sm:mt-0 px-4 py-2 bg-red-600 text-white font-medium rounded-lg text-sm hover:bg-red-500 transition-colors"
          >
            Log Out
          </button>
        </div>

        <div className="mt-8">
          <div className="bg-indigo-50 border border-indigo-100 rounded-xl p-6">
            <h3 className="text-lg font-semibold text-indigo-900 mb-2">Test Core Connectivity</h3>
            <button
              onClick={handleTestRoute}
              className="px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg text-sm hover:bg-indigo-500 transition-colors"
            >
              Verify Secure Handshake
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

const MainApp: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [isRegistering, setIsRegistering] = useState<boolean>(false);

  if (isAuthenticated) {
    return <Dashboard />;
  }

  return isRegistering ? (
    <Register onToggleToLogin={() => setIsRegistering(false)} />
  ) : (
    <Login onToggleToRegister={() => setIsRegistering(true)} />
  );
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
};

export default App;