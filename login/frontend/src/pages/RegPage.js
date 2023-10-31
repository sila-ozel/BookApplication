import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function RegPage({ setAuthority, setLoggedin }) {
    const base_url = `http://localhost:8080`;
    const [name, setName] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("");
    const navigate = useNavigate();
    const [error, setError] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        const containsDigit = /\d/.test(password);
        const containsCaps = /[A-Z]/.test(password);
        if (!containsDigit) {
            setErrorMsg("Password must contain at least one digit.");
            return;
        }
        if (!containsCaps) {
            setErrorMsg("Password must contain at least one capital letter.");
            return;
        }
        const u = {
            username: name,
            password: password,
            role: role
        }
        console.log(name)
        axios.post(`${base_url}/register`, { ...u }, { withCredentials: true })
            .then((response) => {
                setLoggedin(true);
                navigate(role === 'ROLE_ADMIN' ? '/adminpage' : '/userpage');
            })
            .catch((error) => { setError(error); })
        setAuthority(role);
    }

    const handleNameChange = (event) => {
        setName(event.target.value);
    }

    const handlePwChange = (event) => {
        setPassword(event.target.value);
    }

    const handleRoleChange = (event) => {
        setRole(event.target.value);
    }

    return (
        <div className="form">
            {errorMsg && <p>{errorMsg}</p>}
            <form onSubmit={handleSubmit}>
                <h2>Register</h2>
                <div>
                    <div>
                        <label>Username</label>
                        <input type="text" value={name} placeholder="username" required onChange={handleNameChange}></input>
                    </div>
                    <div>
                        <label>Password</label>
                        <input type="password" value={password} min={8} placeholder="password" required onChange={handlePwChange}></input>
                    </div>
                    <div className="radios">
                        <label className="radio-label">User</label>
                        <input className="radio" name="radio" type="radio" required value={"ROLE_USER"} onChange={handleRoleChange}></input>
                        <label className="radio-label">Admin</label>
                        <input className="radio" name="radio" type="radio" required value={"ROLE_ADMIN"} onChange={handleRoleChange}></input>
                    </div>
                    <button className="login-reg" type="submit">Register</button>
                    <p>Already have an account? <a href="/userlogin">Login</a></p>
                </div>
            </form>
        </div>
    );
}