import { NavLink, useNavigate } from "react-router-dom";
import axios from "axios";

const base_url = `http://localhost:8080`;

const Nav = ({ loggedin, setLoggedin, authority }) => {

    const handleLogout = (e) => {
        axios.put(`${base_url}/userlogout`, null, { withCredentials: true }).then((response) => { if (response.status === 200) { setLoggedin(false); } })
    }

    if (loggedin) {
        return (
            <nav>
                <ul>
                    {authority === 'ROLE_ADMIN' && <li><NavLink id="navitem" to={"/set_time_slot"}>Set Time Slot</NavLink></li>}
                    <li>
                        <NavLink id="navitem" to={"/"} onClick={handleLogout}>Logout</NavLink>
                    </li>
                </ul>
            </nav>
        );
    }
    else {
        return (
            <nav>
                <ul>
                    <li>
                        <NavLink id="navitem" to={"/"}>Home</NavLink>
                    </li>
                    <li>
                        <NavLink id="navitem" to={"/userlogin"}>Login</NavLink>
                    </li>
                    <li>
                        <NavLink id="navitem" to={"/register"}>Register</NavLink>
                    </li>
                </ul>
            </nav>
        );
    }
};

export default Nav;