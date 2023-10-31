import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function SetNewTimeSlot() {
    const [time, setTime] = useState(0);
    const base_url = `http://localhost:8080`;
    const navigate = useNavigate();

    const handleNumber = (event) => {
        setTime(event.target.value);
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        axios.post(`${base_url}/SetTimeSlot`,{value:time},{withCredentials:true})
        .then((response) => {navigate("/adminpage");})
    }

    return (
        <div className="form">
            <form className="set-time-slot">
                <h2>Set New Time Slot</h2>
                <input type="number" min={3} value={time} onChange={handleNumber}></input>
                <button className="time-button" type="submit" onClick={handleSubmit}>Set</button>
            </form>
        </div>
    );
}