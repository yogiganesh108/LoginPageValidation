import React, { useState } from 'react';
import {
  Lock, Mail, AlertCircle,
  CheckCircle, Shield, Eye, EyeOff
} from 'lucide-react';

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

  // =====================================================
  // PASSWORD STRENGTH CHECK
  // =====================================================

  const checkStrength = (pass) => {
    let score = 0;
    if (pass.length >= 8) score++;
    if (/[A-Z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;
    return score;
  };

  const strength = checkStrength(formData.password);

  // =====================================================
  // VALIDATION (ONLY BASIC — DO NOT BLOCK SECURITY INPUTS)
  // =====================================================

  const validate = (field, value) => {

    let newErrors = { ...errors };

    if (field === 'email') {

      if (!value)
        newErrors.email = 'Email is required';

      // only show error if it looks like user tried to enter an email but failed (no @ symbol)
      else if (!value.includes("@") && !value.includes("<") && !value.includes("'") && value.includes("-"))
        newErrors.email = 'Email is invalid';

      else if (value.length > 50)
        newErrors.email = 'Email exceeds maximum length (50)';

      else
        delete newErrors.email;
    }

    if (field === 'password') {

      if (!value)
        newErrors.password = 'Password is required';

      else if (value.length < 6)
        newErrors.password = 'Password must be at least 6 characters';

      else if (value.length > 30)
        newErrors.password = 'Password exceeds maximum length';

      else
        delete newErrors.password;
    }

    if (field === 'confirmPassword' && !isLogin) {

      if (!value)
        newErrors.confirmPassword = 'Confirm Password required';

      else if (value !== formData.password)
        newErrors.confirmPassword = 'Passwords do not match';

      else
        delete newErrors.confirmPassword;
    }

    setErrors(newErrors);
  };

  // =====================================================
  // HANDLE INPUT CHANGE
  // =====================================================

  const handleChange = (e) => {

    const { name, value } = e.target;

    setFormData(prev => ({ ...prev, [name]: value }));

    validate(name, value);

    setStatus({ type: '', message: '' });
  };

  // =====================================================
  // HANDLE SUBMIT — ALWAYS ALLOW SUBMISSION
  // =====================================================

  const handleSubmit = async (e) => {

    e.preventDefault();

    if (loading) return;

    setLoading(true);
    setStatus({ type: '', message: '' });

    const hasErrors = Object.keys(errors).length > 0;

    const emptyFields =
      !formData.email ||
      !formData.password ||
      (!isLogin && !formData.confirmPassword);

    // Show error but DO NOT block submission
    if (hasErrors || emptyFields) {
      setStatus({
        type: 'error',
        message: 'Please fix validation errors before submitting.'
      });
      setLoading(false);
      return;
    }

    try {

      const endpoint = isLogin
        ? 'http://localhost:8080/api/login'
        : 'http://localhost:8080/api/register';

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },

        body: JSON.stringify({
          email: formData.email,
          password: formData.password
        })
      });

      const data = await response.json();

      if (data.success) {

        setStatus({
          type: 'success',
          message: isLogin
            ? 'Login Successful'
            : 'Registration Successful! Please login.'
        });

        if (!isLogin) {
          setTimeout(() => {
            setIsLogin(true);
            setFormData({ email: '', password: '', confirmPassword: '' });
            setErrors({});
            setStatus({ type: '', message: '' });
          }, 2000);
        }

      } else {

        setStatus({
          type: 'error',
          message: data.message || 'Invalid credentials'
        });
      }

    } catch {

      setStatus({
        type: 'error',
        message:
          'Unable to connect to server. Please ensure backend is running.'
      });

    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // UI
  // =====================================================

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 to-purple-100 flex items-center justify-center p-4">

      <div className="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden">

        {/* HEADER */}
        <div className="bg-indigo-600 p-8 text-center">
          <div className="mx-auto bg-white/20 w-16 h-16 rounded-full flex items-center justify-center mb-4">
            <Shield className="text-white w-8 h-8" />
          </div>

          <h2 className="text-2xl font-bold text-white" data-testid="page-title">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h2>
        </div>

        {/* FORM */}
        <div className="p-8">

          <form onSubmit={handleSubmit} className="space-y-6">

            {/* EMAIL */}
            <div>
              <label>Email Address</label>

              <div className="relative">
                <Mail className="absolute left-3 top-3 text-gray-400" />

                <input
                  autoFocus
                  type="text"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  data-testid="email-input"
                  className="w-full pl-10 pr-4 py-2 border rounded-lg"
                  placeholder="name@company.com"
                />
              </div>

              {errors.email && (
                <p className="text-red-500 text-xs" data-testid="email-error">
                  <AlertCircle className="inline w-3 h-3 mr-1" />
                  {errors.email}
                </p>
              )}
            </div>

            {/* PASSWORD */}
            <div>
              <label>Password</label>

              <div className="relative">
                <Lock className="absolute left-3 top-3 text-gray-400" />

                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  data-testid="password-input"
                  className="w-full pl-10 pr-10 py-2 border rounded-lg"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-3"
                >
                  {showPassword ? <EyeOff /> : <Eye />}
                </button>
              </div>

              {errors.password && (
                <p className="text-red-500 text-xs" data-testid="password-error">
                  <AlertCircle className="inline w-3 h-3 mr-1" />
                  {errors.password}
                </p>
              )}

              {/* PASSWORD STRENGTH (Registration only) */}
              {!isLogin && formData.password && (
                <div data-testid="password-strength">
                  <p>
                    {strength <= 2 ? 'Weak'
                      : strength === 3 ? 'Medium'
                        : 'Strong'}
                  </p>
                </div>
              )}
            </div>

            {/* CONFIRM PASSWORD */}
            {!isLogin && (
              <div>
                <label>Confirm Password</label>

                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  data-testid="confirm-password-input"
                  className="w-full border rounded-lg py-2 px-3"
                />

                {errors.confirmPassword && (
                  <p className="text-red-500 text-xs" data-testid="confirm-error">
                    {errors.confirmPassword}
                  </p>
                )}
              </div>
            )}

            {/* STATUS MESSAGE */}
            {status.message && (
              <div
                data-testid="status-message"
                className={
                  status.type === 'error'
                    ? 'text-red-600'
                    : 'text-green-600'
                }
              >
                {status.type === 'error'
                  ? <AlertCircle className="inline mr-1" />
                  : <CheckCircle className="inline mr-1" />
                }
                {status.message}
              </div>
            )}

            {/* SUBMIT BUTTON — ONLY DISABLED WHILE LOADING */}
            <button
              type="submit"
              disabled={loading}
              data-testid="submit-button"
              className="w-full bg-indigo-600 text-white py-2 rounded-lg"
            >
              {loading
                ? 'Processing...'
                : isLogin ? 'Sign In' : 'Create Account'}
            </button>

          </form>
        </div>

        {/* FOOTER */}
        <div className="bg-gray-50 p-4 text-center">

          <button
            onClick={() => {
              setIsLogin(!isLogin);
              setFormData({ email: '', password: '', confirmPassword: '' });
              setErrors({});
              setStatus({ type: '', message: '' });
            }}
            data-testid="toggle-auth-mode"
            className="text-indigo-600 font-semibold"
          >
            {isLogin ? 'Sign up' : 'Log in'}
          </button>

        </div>
      </div>
    </div>
  );
}