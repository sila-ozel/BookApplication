import { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";

export default function LoginPage({ setAuthority, setLoggedin }) {
    const base_url = `http://localhost:8080`;
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const userCredentials = { username, password };

        try {
            const response = await axios.post(`${base_url}/userlogin`, userCredentials, { withCredentials: true });
            const user = response.data;

            setAuthority(user.role);
            setLoggedin(true);

            if (user.should_change) {
                navigate('/changepw');
            } else if (user.role === 'ROLE_ADMIN') {
                navigate('/adminpage');
            } else {
                navigate('/userpage');
            }

        } catch (error) {
            console.error("Login error:", error);
            setErrorMsg(
                error?.response?.status === 400
                    ? "Invalid username or password."
                    : "Login failed. Please try again."
            );
        }
    };

    return (
        <div className="form">
            <form onSubmit={handleSubmit}>
                <h2>Login</h2>

                {errorMsg && <p style={{ color: "red" }}>{errorMsg}</p>}

                <div className="formdiv">
                    <div>
                        <label>Username</label>
                        <input
                            type="text"
                            name="username"
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
                            name="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter password"
                            required
                        />
                    </div>

                    <button className="login-reg" type="submit">Login</button>
                    <p>Don't have an account? <Link to="/register">Register</Link></p>
                </div>
            </form>
        </div>
    );
}
