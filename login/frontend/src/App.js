import './App.css';
import './components/Nav';
import { Route, Routes } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegPage from './pages/RegPage';
import Nav from './components/Nav';
import { useState } from 'react';
import React from 'react';
import ProtectedRoute from './components/ProtectedRoute';


function App() {
  const [authority, setAuthority] = useState("ROLE_USER"); //default value is user
  const [loggedin, setLoggedin] = useState(false);

  return (
    <div className='App'>
      <Nav loggedin={loggedin} setLoggedin={setLoggedin} authority={authority}></Nav>
      <div className='routes'>
        <Routes>
          <Route path='/userlogin' element={<LoginPage setAuthority={setAuthority} setLoggedin={setLoggedin} />}></Route>
          <Route path='/register' element={<RegPage setAuthority={setAuthority} setLoggedin={setLoggedin} />}></Route>
          <Route path='*' element={<ProtectedRoute authority={authority} loggedin={loggedin}></ProtectedRoute>}></Route>
        </Routes>
      </div>
    </div>

  );
}

export default App;
