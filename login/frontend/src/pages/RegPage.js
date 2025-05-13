import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function RegPage({ setAuthority, setLoggedin }) {
    const base_url = `http://localhost:8080`;
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const navigate = useNavigate();

    const validatePassword = (pw) => {
        if (pw.length < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!/\d/.test(pw)) {
            return "Password must contain at least one digit.";
        }
        if (!/[A-Z]/.test(pw)) {
            return "Password must contain at least one capital letter.";
        }
        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const validationError = validatePassword(password);
        if (validationError) {
            setErrorMsg(validationError);
            return;
        }

        const userData = { username, password, role };

        try {
            const response = await axios.post(`${base_url}/register`, userData, { withCredentials: true });
            setLoggedin(true);
            setAuthority(role);
            navigate(role === 'ROLE_ADMIN' ? '/adminpage' : '/userpage');
        } catch (err) {
            setErrorMsg(err?.response?.status === 409
                ? "User already exists. Try a different username."
                : "Registration failed. Please try again.");
        }
    };

    return (
        <div className="form">
            <form onSubmit={handleSubmit}>
                <h2>Register</h2>

                {errorMsg && <p style={{ color: "red" }}>{errorMsg}</p>}

                <div>
                    <label>Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Enter username"
                        required
                    />
                </div>

                <div>
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter password"
                        required
                    />
                </div>

                <div className="radios">
                    <label className="radio-label">User</label>
                    <input
                        type="radio"
                        name="role"
                        value="ROLE_USER"
                        onChange={(e) => setRole(e.target.value)}
                        required
                    />
                    <label className="radio-label">Admin</label>
                    <input
                        type="radio"
                        name="role"
                        value="ROLE_ADMIN"
                        onChange={(e) => setRole(e.target.value)}
                        required
                    />
                </div>

                <button className="login-reg" type="submit">Register</button>
                <p>Already have an account? <a href="/userlogin">Login</a></p>
            </form>
        </div>
    );
}
