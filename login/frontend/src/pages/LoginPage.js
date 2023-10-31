import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function LoginPage({ setAuthority, setLoggedin }) {
    const base_url = `http://localhost:8080`;
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [user, setUser] = useState(null);
    const navigate = useNavigate();
    const [error, setError] = useState(null);
    const [errMsg, setErrMsg] = useState("");

    const handleUsernameChange = (event) => {
        setUsername(event.target.value);
    }
    const handlePwChange = (event) => {
        setPassword(event.target.value);
    }
    const handleSubmit = async (event) => {
        event.preventDefault();
        const u = {
            username: username,
            password: password
        }
        axios.post(`${base_url}/userlogin`, { ...u }, { withCredentials: true })
            .then((response) => {
                setUser(response.data); setAuthority(response.data.role);
                setLoggedin(true);
                navigate(response.data.should_change === true ? '/changepw' : (response.data.role === 'ROLE_ADMIN' ? '/adminpage' : '/userpage'));
            }
            ) //if the user is successfully logged in
            .catch((error) => {
                setError(error);
                if (error.code === 'ERR_BAD_REQUEST') { setErrMsg("Invalid credentials"); }
            });
    }

    return (
        <div className="form">
            <form onSubmit={handleSubmit}>
                <h2>Login</h2>
                <div className="formdiv">
                    {errMsg && <p>{errMsg}</p>}
                    <div>
                        <label>Username</label>
                        <input type="text" name="username" value={username} placeholder="username" required onChange={handleUsernameChange}></input>
                    </div>
                    <div>
                        <label>Password</label>
                        <input type="password" name="password" value={password} min={8} placeholder="password" required onChange={handlePwChange}></input>
                    </div>
                    <button className="login-reg" type="submit">Login</button>
                    <p>Don't have an account? <a href="/register">Register</a></p>
                </div>

            </form>
        </div>
    );
}