import axios from "axios";
import { useState } from "react";
import { Route, Routes, useNavigate } from "react-router-dom";

const base_url = `http://localhost:8080`;
const ChangePw = () => {
    const [password, setPassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    const navigate = useNavigate();
    const handlePassword = (event) => {
        setPassword(event.target.value);
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
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
        const response = await axios.post(`${base_url}/changepw`, { pw: password }, { withCredentials: true });
        if (response.status === 409) {
            console.log("the password must be different than the last 3 passwords");
        }
        if (response.status === 200) {
            navigate(response.data.role === 'ROLE_ADMIN' ? '/adminpage' : '/userpage');
        }
    }

    return (
        <div className="form">
            <form className="change-pw" onSubmit={handleSubmit}>
                <h2>Change Password</h2>
                {errorMsg && <p>{errorMsg}</p>}
                <label>Password</label>
                <input type="password" placeholder="password" required onChange={handlePassword}></input>
                <button type="submit">Change</button>
            </form>
        </div>
    );
}

export default ChangePw;