import React, { useState } from 'react';
import { signup } from '../services/api';
import './AuthForm.css'; // Reuse or create new styles

const SignupPage = ({ navigate }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        try {
            const response = await signup(email, password);
            setMessage(response.data.message || "Signup successful! Please login.");
            // Optionally navigate to login after a delay or directly
            // navigate('login');
        } catch (error) {
            setMessage(error.response?.data?.message || "Signup failed. Please try again.");
        }
    };

    return (
        <div className="auth-form-container page-container">
            <h2>Get Started Now</h2>
            {message && <p className={`message ${message.toLowerCase().includes("error") || message.toLowerCase().includes("failed") ? 'error' : 'success'}`}>{message}</p>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="email">Email Address</label>
                    <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <button type="submit" className="auth-button">Sign Up</button>
            </form>
            <button onClick={() => navigate('login')} className="switch-auth-link">
                Already have an account? Login
            </button>
        </div>
    );
};
export default SignupPage;
