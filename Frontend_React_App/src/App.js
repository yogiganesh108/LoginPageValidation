import React, { useState, useEffect } from 'react';
import { User, Lock, Mail, AlertCircle, CheckCircle, Shield, Eye, EyeOff } from 'lucide-react';

export default function App() {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [status, setStatus] = useState({ type: '', message: '' });
  const [loading, setLoading] = useState(false);

  // Password strength checking
  const checkStrength = (pass) => {
    let score = 0;
    if (pass.length >= 8) score++;
    if (/[A-Z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;
    return score;
  };

  const strength = checkStrength(formData.password);

  // Input validation
  const validate = (field, value) => {
    let newErrors = { ...errors };

    // SQL Injection basic pattern detection for demonstration
    const sqlInjectionPattern = /('|"|;|--|\/\*|\*\/)/;

    if (field === 'email') {
      if (!value) newErrors.email = 'Email is required';
      else if (!/\S+@\S+\.\S+/.test(value)) newErrors.email = 'Email is invalid';
      else if (value.length > 50) newErrors.email = 'Email exceeds maximum length (50)';
      else if (sqlInjectionPattern.test(value)) newErrors.email = 'Invalid characters detected (Security)';
      else delete newErrors.email;
    }

    if (field === 'password') {
      if (!value) newErrors.password = 'Password is required';
      else if (value.length < 6) newErrors.password = 'Password must be at least 6 characters';
      else if (value.length > 30) newErrors.password = 'Password exceeds maximum length';
      else delete newErrors.password;
    }

    if (field === 'confirmPassword' && !isLogin) {
      if (value !== formData.password) newErrors.confirmPassword = 'Passwords do not match';
      else delete newErrors.confirmPassword;
    }

    setErrors(newErrors);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    validate(name, value);
    setStatus({ type: '', message: '' }); // Clear status on type
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setStatus({ type: '', message: '' });

    // Validate form
    const hasErrors = Object.keys(errors).length > 0;
    const emptyFields = !formData.email || !formData.password || (!isLogin && !formData.confirmPassword);

    if (hasErrors || emptyFields) {
      setStatus({ type: 'error', message: 'Please fix validation errors before submitting.' });
      setLoading(false);
      return;
    }

    try {
      // Call backend API
      const endpoint = isLogin ? 'http://localhost:8080/api/login' : 'http://localhost:8080/api/register';
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: formData.email,
          password: formData.password,
        }),
      });

      const data = await response.json();

      if (data.success) {
        setStatus({ 
          type: 'success', 
          message: isLogin ? 'Login Successful! Redirecting...' : 'Registration Successful! Please login.' 
        });
        
        // Clear form on successful registration
        if (!isLogin) {
          setTimeout(() => {
            setIsLogin(true);
            setFormData({ email: '', password: '', confirmPassword: '' });
            setErrors({});
            setStatus({ type: '', message: '' });
          }, 2000);
        }
      } else {
        setStatus({ type: 'error', message: data.message || 'Operation failed.' });
      }
    } catch (error) {
      console.error('Error:', error);
      setStatus({ 
        type: 'error', 
        message: 'Unable to connect to server. Please ensure the backend API is running on port 8080.' 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 to-purple-100 flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden">
        {/* Header */}
        <div className="bg-indigo-600 p-8 text-center">
          <div className="mx-auto bg-white/20 w-16 h-16 rounded-full flex items-center justify-center mb-4 backdrop-blur-sm">
            <Shield className="text-white w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-white mb-2" data-testid="page-title">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h2>
          <p className="text-indigo-100 text-sm">
            {isLogin ? 'Secure access to your dashboard' : 'Join our secure platform today'}
          </p>
        </div>

        {/* Form */}
        <div className="p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            
            {/* Email Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
              <div className="relative">
                <Mail className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  data-testid="email-input"
                  className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 transition-colors ${
                    errors.email 
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-200' 
                      : 'border-gray-300 focus:border-indigo-500 focus:ring-indigo-200'
                  }`}
                  placeholder="name@company.com"
                />
              </div>
              {errors.email && (
                <p className="text-red-500 text-xs mt-1 flex items-center" data-testid="email-error">
                  <AlertCircle className="w-3 h-3 mr-1" /> {errors.email}
                </p>
              )}
            </div>

            {/* Password Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  data-testid="password-input"
                  className={`w-full pl-10 pr-10 py-2 border rounded-lg focus:ring-2 transition-colors ${
                    errors.password
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-200'
                      : 'border-gray-300 focus:border-indigo-500 focus:ring-indigo-200'
                  }`}
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-3 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-4 h-4"/> : <Eye className="w-4 h-4"/>}
                </button>
              </div>
              {errors.password && (
                <p className="text-red-500 text-xs mt-1 flex items-center" data-testid="password-error">
                  <AlertCircle className="w-3 h-3 mr-1" /> {errors.password}
                </p>
              )}
              
              {/* Password Strength Meter (Bonus Requirement) */}
              {!isLogin && formData.password && (
                <div className="mt-2" data-testid="password-strength">
                  <div className="flex gap-1 h-1 mb-1">
                    {[1, 2, 3, 4].map((i) => (
                      <div 
                        key={i} 
                        className={`flex-1 rounded-full transition-all duration-300 ${
                          strength >= i 
                            ? (strength <= 2 ? 'bg-red-400' : strength === 3 ? 'bg-yellow-400' : 'bg-green-400')
                            : 'bg-gray-200'
                        }`}
                      />
                    ))}
                  </div>
                  <p className="text-xs text-gray-500 text-right">
                    {strength <= 2 ? 'Weak' : strength === 3 ? 'Medium' : 'Strong'}
                  </p>
                </div>
              )}
            </div>

            {/* Confirm Password (Registration Only) */}
            {!isLogin && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
                <div className="relative">
                  <Lock className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                  <input
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    data-testid="confirm-password-input"
                    className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 transition-colors ${
                      errors.confirmPassword
                        ? 'border-red-300 focus:border-red-500 focus:ring-red-200'
                        : 'border-gray-300 focus:border-indigo-500 focus:ring-indigo-200'
                    }`}
                    placeholder="••••••••"
                  />
                </div>
                {errors.confirmPassword && (
                  <p className="text-red-500 text-xs mt-1 flex items-center" data-testid="confirm-error">
                    <AlertCircle className="w-3 h-3 mr-1" /> {errors.confirmPassword}
                  </p>
                )}
              </div>
            )}

            {/* Status Message */}
            {status.message && (
              <div 
                data-testid="status-message"
                className={`p-3 rounded-lg text-sm flex items-start ${
                  status.type === 'error' ? 'bg-red-50 text-red-700' : 'bg-green-50 text-green-700'
                }`}
              >
                {status.type === 'error' ? <AlertCircle className="w-4 h-4 mr-2 mt-0.5" /> : <CheckCircle className="w-4 h-4 mr-2 mt-0.5" />}
                {status.message}
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              data-testid="submit-button"
              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2.5 rounded-lg transition-colors focus:ring-4 focus:ring-indigo-200 disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                isLogin ? 'Sign In' : 'Create Account'
              )}
            </button>
          </form>
        </div>

        {/* Footer */}
        <div className="bg-gray-50 p-4 text-center border-t border-gray-100">
          <p className="text-sm text-gray-600">
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <button
              onClick={() => {
                setIsLogin(!isLogin);
                setFormData({ email: '', password: '', confirmPassword: '' });
                setErrors({});
                setStatus({ type: '', message: '' });
              }}
              data-testid="toggle-auth-mode"
              className="text-indigo-600 font-semibold hover:text-indigo-700 transition-colors"
            >
              {isLogin ? 'Sign up' : 'Log in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
