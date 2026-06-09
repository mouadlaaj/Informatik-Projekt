import React, { useState } from "react";
import axios from "axios";
import { Button, Input } from "@mui/joy";
import TextField from "@mui/material/TextField";
import { registerUser } from "../service/service-call";
import RemoveRedEyeIcon from "@mui/icons-material/RemoveRedEye";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { InputAdornment } from "@mui/material";
import { Person, Lock, Email, Phone, Home, Group, CreditCard } from "@mui/icons-material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";

import { FormControl, MenuItem, Select, InputLabel } from "@mui/material";
import { toast } from "react-toastify";
import { FaIdCard } from "react-icons/fa";

const RegisterPage = () => {
  const [isPasswordVisible, setPasswordVisible] = React.useState(false);
  const [value, setValue] = React.useState(null);

  const [formData, setFormData] = useState({
    memberId: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    password: "",
    emailId: "",
    role: "MANAGER",
    designation: "",
    address: "",
    dob: "",
    gender: "",
  });

  const togglePasswordVisibility = () => {
    setPasswordVisible(!isPasswordVisible);
  };

  const handleChange = (e) => {
    const name = e.target.name;
    const value = e.target.value;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await registerUser(formData);
      window.location.href = "/login";
      toast.success("Registration successfull");
    } catch (error) {
      if (
        error &&
        error.response &&
        error.response.data &&
        error.response.data.message
      ) {
        toast.error(error.response.data.message);
      } else if (
        error.response &&
        error.response.data &&
        error.response.data.errors &&
        error.response.data.errors.length > 0
      ) {
        toast.error(error.response.data.errors[0]);
      } else {
        toast.error("Internal server error, contact support team");
      }
    }
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 h-screen w-full overflow-hidden">
      <div
        className="relative flex flex-col justify-center items-center text-white px-8 py-16 overflow-hidden"
        style={{ backgroundColor: "#1976d2", minHeight: "100vh" }}
      >
        {/* Floating background circles */}
        <div className="absolute top-[-50px] right-[-50px] w-[200px] h-[200px] rounded-full bg-white/10 animate-float"></div>

        <div className="absolute bottom-[-100px] left-[-100px] w-[300px] h-[300px] rounded-full bg-white/5 animate-float-reverse"></div>

        {/* Content */}
        <div className="text-center z-10 animate-fade-in">
          <h1 className="text-4xl font-bold mb-4 drop-shadow-md">Welcome</h1>
          <p className="text-lg max-w-md mx-auto opacity-90 leading-relaxed drop-shadow-sm">
            Access your dashboard, manage your tasks, and stay productive with
            our comprehensive platform.
          </p>
        </div>
      </div>

      <div
        className="w-full max-w-4xl p-10 overflow-y-auto h-screen "
        style={{
          backgroundColor: "rgba(255,255,255,0.1)",
          animation: "float 6s ease-in-out infinite",
        }}
      >
        <div className="p-10 shadow-2xl rounded-xl bg-white animate-[slide-in_1.2s_ease-in-out]">
          <div className="text-center mb-8">
            <h2
              className="text-3xl font-extrabold"
              style={{ color: "#1976d2" }}
            >
              Create an Account
            </h2>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            {/* Row 1 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <InputField
                id="firstName"
                label="First Name"
                value={formData.firstName}
                onChange={handleChange}
                startIcon={<Person color="action" />}
                className="h-2 w-full md:w-60"
              />
              <InputField
                id="lastName"
                label="Last Name"
                value={formData.lastName}
                onChange={handleChange}
                startIcon={<Person color="action" />}
              />
            </div>

            {/* Row 2 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <InputField
                id="memberId"
                label="Member ID"
                value={formData.memberId}
                onChange={handleChange}
                startIcon={<FaIdCard color="action" />}
              />
              <InputField
                id="emailId"
                type="email"
                label="Email"
                value={formData.emailId}
                onChange={handleChange}
                startIcon={<Email color="action" />}
              />
            </div>

            {/* Row 3 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <InputField
                id="phoneNumber"
                label="Phone Number"
                value={formData.phoneNumber}
                onChange={handleChange}
                startIcon={<Phone color="action" />}
              />

              <div className="relative">
                <TextField
                  id="password"
                  name="password"
                  label="Password"
                  type={isPasswordVisible ? "text" : "password"}
                  value={formData.password}
                  onChange={handleChange}
                  fullWidth
                  variant="outlined"
                  size="small"
                  margin="dense"
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Lock color="action" />
                      </InputAdornment>
                    ),
                  }}
                />

                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 focus:outline-none"
                >
                  {isPasswordVisible ? (
                    <RemoveRedEyeIcon style={{ fontSize: "16px" }} />
                  ) : (
                    <VisibilityOffIcon style={{ fontSize: "16px" }} />
                  )}
                </button>
              </div>
            </div>

            {/* Row 4 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <SelectField
                id="role"
                label="Role"
                value={formData.role}
                onChange={handleChange}
                startIcon={<Group color="action" />}
                options={["MANAGER", "EMPLOYEE"]}
              />

              {formData.role === "EMPLOYEE" && (
                <SelectField
                  id="designation"
                  label="Designation"
                  value={formData.designation}
                  onChange={handleChange}
                  options={["DESIGNER", "DEVELOPER", "TESTER"]}
                />
              )}
            </div>

            {/* Address */}
            <div className="relative">
              <TextField
                id="address"
                name="address"
                label="Address"
                value={formData.address}
                onChange={handleChange}
                multiline
                rows={3}
                fullWidth
                variant="outlined"
                size="small"
                margin="dense"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Home color="action" />
                    </InputAdornment>
                  ),
                }}
              />
            </div>

            {/* Row 6 */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DatePicker
                  label="Date of Birth"
                  value={formData.dob ? dayjs(formData.dob) : null}
                  onChange={(newValue) => {
                    setFormData({
                      ...formData,
                      dob: newValue ? newValue.format("YYYY-MM-DD") : "",
                    });
                  }}
                  maxDate={dayjs()}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      fullWidth
                      size="small"
                      margin="dense"
                    // sx={{
                    //   "& .MuiInputBase-root": {
                    //     height: 32,
                    //     fontSize: "0.75rem",
                    //   },
                    //   "& .MuiInputLabel-root": {
                    //     fontSize: "0.75rem",
                    //   },
                    // }}
                    />
                  )}
                />
              </LocalizationProvider>

              {/* Gender */}
              <SelectField
                id="gender"
                label="Gender"
                value={formData.gender}
                onChange={handleChange}
                options={["Male", "Female", "Other"]}
              />
            </div>

            {/* Submit */}
            <div className="pt-6">
              <Button
                type="submit"
                fullWidth
                variant="solid"
                sx={{ backgroundColor: "bg-gray-300 hover:bg-gray-400" }}
              >
                Register
              </Button>
            </div>

            <p className="text-center text-sm text-gray-600 mt-4">
              Already have an account?{" "}
              <a href="/login" className="text-blue-600 ">
                Sign in
              </a>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
};

// Reusable InputField component

const InputField = ({
  id,
  label,
  type = "text",
  value,
  onChange,
  startIcon = null,
}) => (
  <TextField
    id={id}
    name={id}
    label={label}
    type={type}
    value={value}
    onChange={onChange}
    fullWidth
    variant="outlined"
    size="small"
    margin="dense"
    InputProps={{
      startAdornment: startIcon ? (
        <InputAdornment position="start">{startIcon}</InputAdornment>
      ) : null,
    }}
  />
);

const SelectField = ({ id, label, value, onChange, options }) => (
  <FormControl fullWidth size="small" sx={{ mt: 0.5 }}>
    <InputLabel id={`${id}-label`} sx={{ fontSize: "0.75rem" }}>
      {label}
    </InputLabel>
    <Select
      labelId={`${id}-label`}
      name={id}
      value={value}
      label={label}
      onChange={onChange}

    >
      <MenuItem value="">
        <em>Select {label}</em>
      </MenuItem>
      {options.map((option, idx) => (
        <MenuItem key={idx} value={option} sx={{ fontSize: "0.75rem" }}>
          {option}
        </MenuItem>
      ))}
    </Select>
  </FormControl>
);

export default RegisterPage;
