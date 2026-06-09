import{ useState } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Paper,
  Grid,
  InputAdornment,
  IconButton,
  CircularProgress,
  Fade,
  Slide,
  Link
} from "@mui/material";
import {
  Visibility,
  VisibilityOff,
  Person,
  Lock
} from "@mui/icons-material";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { loginUser } from "../service/service-call";

export default function Login() {
  const [loginLoad, setLoginLoad] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleUsernameChange = (event) => {
    setUsername(event.target.value);
    if (errorMessage) setErrorMessage("");
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
    if (errorMessage) setErrorMessage("");
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      toast.error("Please fill in all fields");
      return;
    }

    setLoginLoad(true);
    setErrorMessage("");

    try {
      const response = await loginUser(
        username,
        password,
      );

      if (response.status === 401) {
        setErrorMessage("Invalid credentials");
        toast.error("Invalid credentials");
      } else {
        const data = response.data;
        console.log("Login Response:========================", data);

        if (data?.email) {
          localStorage.setItem("firstname", data.firstName);
          localStorage.setItem("lastname", data.lastName);
          localStorage.setItem("email", data.email);
          localStorage.setItem("userId", data.id);
          localStorage.setItem("gender", data.gender);
          localStorage.setItem("token", data.token);
          localStorage.setItem("role", data.role);
          localStorage.setItem("isTL", data.teamLead);
          localStorage.setItem("designation", data.designation);

          toast.success("Login successful!");
          window.location.replace("/dashboard");
        } else {
          setErrorMessage("Invalid credentials");
          toast.error("Invalid credentials");
        }
      }
    } catch (error) {
      if (error && error.response && error.response.data && error.response.data.message) {
        toast.error(error.response.data.message);
      } else if (error.response && error.response.data && error.response.data.errors && error.response.data.errors.length > 0) {
        toast.error(error.response.data.errors[0]);
      } else {
        toast.error("Internal server error, contact support team");
      }
      
    } finally {
      setLoginLoad(false);
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      handleLogin();
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        backgroundColor: "#f5f5f5",
      }}
    >


      <Grid container sx={{ minHeight: "100vh" }}>
        <Grid
          item
          xs={12}
          md={6}
          sx={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: "#1976d2",
            color: "white",
            padding: 4,
            position: "relative",
            overflow: "hidden",
          }}
        >
          <Fade in timeout={1000}>
            <Box sx={{ textAlign: "center", zIndex: 1 }}>
              <Typography
                variant="h3"
                component="h1"
                gutterBottom
                sx={{
                  fontWeight: "bold",
                  mb: 3,
                  textShadow: "2px 2px 4px rgba(0,0,0,0.3)",
                }}
              >
                Welcome Back
              </Typography>
              <Typography
                variant="h6"
                sx={{
                  opacity: 0.9,
                  lineHeight: 1.6,
                  maxWidth: 400,
                  textShadow: "1px 1px 2px rgba(0,0,0,0.3)",
                }}
              >
                Access your dashboard, manage your tasks, and stay productive with our comprehensive platform.
              </Typography>
            </Box>
          </Fade>

          <Box
            sx={{
              position: "absolute",
              top: -50,
              right: -50,
              width: 200,
              height: 200,
              backgroundColor: "rgba(255,255,255,0.1)",
              borderRadius: "50%",
              animation: "float 6s ease-in-out infinite",
            }}
          />
          <Box
            sx={{
              position: "absolute",
              bottom: -100,
              left: -100,
              width: 300,
              height: 300,
              backgroundColor: "rgba(255,255,255,0.05)",
              borderRadius: "50%",
              animation: "float 8s ease-in-out infinite reverse",
            }}
          />
        </Grid>

        <Grid
          item
          xs={12}
          md={6}
          sx={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            padding: 4,
          }}
        >
          <Slide direction="left" in timeout={800}>
            <Paper
              elevation={8}
              sx={{
                padding: 4,
                borderRadius: 3,
                width: "100%",
                maxWidth: 400,
                backgroundColor: "white",
                boxShadow: "0 10px 30px rgba(0,0,0,0.1)",
              }}
            >
              <Box sx={{ textAlign: "center", mb: 4 }}>
                <Typography
                  variant="h4"
                  component="h2"
                  sx={{
                    fontWeight: "bold",
                    color: "#1976d2",
                    mb: 1,
                  }}
                >
                  Sign In
                </Typography>
                <Typography
                  variant="body2"
                  sx={{
                    color: "text.secondary",
                  }}
                >
                  Please enter your credentials to continue
                </Typography>
              </Box>

              

              <Box component="form" noValidate>
                <TextField
                  size="small"
                  fullWidth
                  label="Username"
                  variant="outlined"
                  value={username}
                  onChange={handleUsernameChange}
                  onKeyPress={handleKeyPress}
                  sx={{ mb: 2 }}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Person color="action" />
                      </InputAdornment>
                    ),
                  }}
                  disabled={loginLoad}
                />

                <TextField
                  fullWidth
                  size="small"
                  label="Password"
                  type={showPassword ? "text" : "password"}
                  variant="outlined"
                  value={password}
                  onChange={handlePasswordChange}
                  onKeyPress={handleKeyPress}
                  sx={{ mb: 3 }}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <Lock color="action" />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          size="small"
                          onClick={togglePasswordVisibility}
                          edge="end"
                          disabled={loginLoad}
                        >
                          {showPassword ? <Visibility /> : <VisibilityOff />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  disabled={loginLoad}
                />

                <Button
                  fullWidth
                  variant="contained"
                  size="small"
                  onClick={handleLogin}
                  disabled={loginLoad}
                  sx={{
                    py: 0.5,
                    mb: 2,
                    borderRadius: 2,
                    textTransform: "none",
                    fontSize: "1.1rem",
                    fontWeight: "bold",
                    background: "linear-gradient(45deg, #1976d2, #42a5f5)",
                    "&:hover": {
                      background: "linear-gradient(45deg, #1565c0, #1976d2)",
                    },
                  }}
                >
                  {loginLoad ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : (
                    "Sign In"
                  )}
                </Button>

                <Box sx={{ textAlign: "center" }}>
                  <Typography variant="body2" color="text.secondary">
                    Don't have an account?{" "}
                    <Link
                      href="/register"
                      sx={{
                        color: "#1976d2",
                        textDecoration: "none",
                        fontWeight: "bold",
                        "&:hover": {
                          textDecoration: "underline",
                        },
                      }}
                    >
                      Register
                    </Link>
                  </Typography>
                </Box>
              </Box>
            </Paper>
          </Slide>
        </Grid>
      </Grid>


      <style jsx>{`
        @keyframes float {
          0%, 100% {
            transform: translateY(0px);
          }
          50% {
            transform: translateY(-20px);
          }
        }
      `}</style>
    </Box>
  );
}