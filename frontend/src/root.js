
import { Route, Routes, Navigate } from 'react-router-dom';
import Home from "./Pages/Home";
import Dashboard from "./Pages/Dashboard";
import Login from "./Pages/Login";
import Manage from "./Pages/Manage";
import Reports from "./Components/Reports/report";
import MainLayout from './MainLayout';
import Register from './Pages/Register';

function Root() {
  const isLoggedIn = localStorage.getItem("userId");

  return (
    <>
      {isLoggedIn ? (
        <MainLayout>
          <Routes>
            <Route path="/" element={<Home />} />
             <Route path="/board" element={<Home />} />
            <Route path="/manage" element={<Manage />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/reports" element={<Reports />} />
        </Routes>
        </MainLayout>
      ) : (
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="*" element={<Navigate to="/" />} />
          <Route path="/register" element={<Register />} />

        </Routes>
      )}
    </>
  );
}

export default Root;
