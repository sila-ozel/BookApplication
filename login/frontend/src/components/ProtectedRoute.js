import { Route, Routes, Navigate } from "react-router-dom";
import React from "react";
import Home from '../pages/Home';
import UserPage from '../pages/UserPage';
import AdminPage from "../pages/AdminPage";
import ChangePw from "../pages/ChangePw";
import SetNewTimeSlot from "../pages/SetNewTimeSlot";

const ProtectedRoute = ({ authority,loggedin }) => {
    return (
        <div>
            <Routes>
                <Route path="/" element = {<Home/>}></Route>
                {loggedin && authority === 'ROLE_USER' && <Route path="/userpage" element = {<UserPage></UserPage>}></Route>}
                {loggedin && authority === 'ROLE_ADMIN' && <Route path="/adminpage" element= {<AdminPage></AdminPage>}></Route>}
                {loggedin && authority === 'ROLE_ADMIN' && <Route path="/set_time_slot" element={<SetNewTimeSlot></SetNewTimeSlot>}></Route>}
                {loggedin && <Route path="/changepw" element={<ChangePw></ChangePw>}></Route>}
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </div>
    )
}

export default ProtectedRoute;