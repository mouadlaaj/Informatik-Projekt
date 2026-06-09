import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import KeyboardArrowLeft from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRight from '@mui/icons-material/KeyboardArrowRight';
import Radio from '@mui/joy/Radio';
import RadioGroup from '@mui/joy/RadioGroup';
import Sheet from '@mui/joy/Sheet';
import { Divider, FormControl, FormControlLabel, FormLabel, Grid, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import DialogContent from '@mui/material/DialogContent';
import MobileStepper from '@mui/material/MobileStepper';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import { DesktopDatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import moment from 'moment';
import * as React from 'react';
import { FaClipboardUser } from "react-icons/fa6";
import { HiUserGroup } from "react-icons/hi2";
import { RiLockPasswordFill } from "react-icons/ri";
import { toast } from 'react-toastify';
import { SuperAdminUserList, editNewMember, getMemberById, logout } from '../../service/service-call';


export default function EditMembers({ editMemberModal, selectedMemberId }) {

    React.useEffect(() => {
        getMemberById(selectedMemberId).then(resp => {

            if (resp.status === 401) {
                logout();
            }
            resp.json().then(data => {

                setFName(data.firstName);
                setLName(data.lastName);
                setMobile(data.phoneNumber);
                setEmail(data.emailId);
                setDesignation(data.designation);
                setDob(data.dob);
                setDoj(data.dateOfJoining);

                if (data.yearsOfExperience.toString().includes(".")) {
                    let value = data.yearsOfExperience.toString();
                    let valSplit = value.split(".");
                    setYears(valSplit[0]);
                    setMonths(valSplit[1]);
                    getOverallExperience(data.dateOfJoining, valSplit[0], valSplit[1]);
                } else {
                    setYears(data.yearsOfExperience.toString());
                    setMonths(0);
                    getOverallExperience(data.dateOfJoining, data.yearsOfExperience.toString(), 0);
                }

                setExperience(data.yearsOfExperience);
                setAddress(data.address);
                setGender(data.gender);
                setRole(data.role);
                setPassword(data.firstName);
                setMemberId(data.id);
            });
        }).catch(error => {
            console.log("login user err " + error);
        });
    }, []);
    let roleObj = [
        {
            label: "MANAGER",
            value: "MANAGER"
        },
        {
            label: "EMPLOYEE",
            value: "EMPLOYEE"
        },
    ]

    if (localStorage.getItem("role") === "EMPLOYEE") {
        roleObj = [];
        roleObj.push({
            label: "User",
            value: "EMPLOYEE"
        });
    }
    const [overallExperience, setOverallExperience] = React.useState("0 years, 0 months");
    const [year, setYears] = React.useState(0);
    const [month, setMonths] = React.useState(0);
    const [fname, setFName] = React.useState('');
    const [lname, setLName] = React.useState('');
    const [mobile, setMobile] = React.useState('');
    const [email, setEmail] = React.useState('');
    const [role, setRole] = React.useState('');
    const [memberId, setMemberId] = React.useState('');
    const [password, setPassword] = React.useState('');
    const [designation, setDesignation] = React.useState('');
    const [dob, setDob] = React.useState('');
    const [doj, setDoj] = React.useState(null);
    const [gender, setGender] = React.useState('');
    const [experience, setExperience] = React.useState(0);
    const [address, setAddress] = React.useState("");
    const handleDesignationChange = (e) => {
        setDesignation(e.target.value);
    };

    const handleRoleChange = (e) => {
        setRole(e.target.value);
    };


    const handleFirstNameChange = (e) => {
        setFName(e.target.value);
    };

    const handleLastNameChange = (e) => {
        setLName(e.target.value);
    };

    const handleIdChange = (e) => {
        setMemberId(e.target.value);
    };

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    };

    const handleMobileChange = (e) => {
        setMobile(e.target.value);
    };

    const handleEmailChange = (e) => {
        setEmail(e.target.value);
    };

    const handleDobChange = (e) => {
        setDob(e)
    }
    const handleDojChange = (e) => {
        setDoj(e);
        getOverallExperience(e, year, month);
    }

    const handleMonthChange = (e) => {
        setMonths(e.target.value);
        setExperience(year + "." + e.target.value);
        getOverallExperience(doj, year, e.target.value);
    }

    const handleYearChange = (e) => {
        setYears(e.target.value);
        setExperience(e.target.value + "." + month);
        getOverallExperience(doj, e.target.value, month);
    }

    const handleGenderChange = (event) => {
        setGender(event.target.value);
    };

  

    const handleAddressChange = (e) => {
        setAddress(e.target.value);
    };

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
   

    function validateBasic() {
        if (fname === "" || fname === undefined || lname === "" || lname === undefined ||
            mobile === "" || mobile === undefined || email === "" || email === undefined) {
            toast.error('Fields cannot be blank');
            return false;
        } else {
            return true;
        }
    }

    function validateCredential() {
        if (memberId === "" || memberId === undefined || password === "" || password === undefined) {
            toast.error('Member credentials cannot be blank');
            return false;
        } else {
            return true;
        }
    }
    const getOverallExperience = (dateString, oldExperienceYears, oldExperienceMonths) => {
        var joinDate = moment(dateString);

        if (!oldExperienceYears) {
            oldExperienceYears = Number(0);
        }
        if (!oldExperienceMonths) {
            oldExperienceMonths = Number(0);
        }
    
        var currentDate = moment();
        var newExperience = moment.duration(currentDate.diff(joinDate));

        
        var yearsExperience = Number(newExperience.years()) + Number(oldExperienceYears);
        var monthsExperience = Number(newExperience.months()) + Number(oldExperienceMonths);

        
        if (monthsExperience >= 12) {
            yearsExperience += Math.floor(monthsExperience / 12);
            monthsExperience = monthsExperience % 12;
        }


        setOverallExperience(Number(yearsExperience) + " years, " + monthsExperience + " months");
    };

   function addNewMembers() {
  if (
    fname === "" || fname === undefined ||
    lname === "" || lname === undefined ||
    designation === "" || designation === undefined ||
    role === "" || role === undefined ||
    mobile === "" || mobile === undefined ||
    email === "" || email === undefined
  ) {
    toast.error('Fields cannot be blank');
  } else {
    const formattedDoj = doj ? moment(doj).format("YYYY-MM-DD") : null;
    const formattedDob = dob ? moment(dob).format("YYYY-MM-DD") : null;

    editNewMember(
      memberId,
      password,
      fname,
      lname,
      mobile,
      email,
      designation,
      role,
      address,
      experience,
      formattedDoj,
      formattedDob,
      gender
    )
      .then(resp => {
        toast.success('Member updated successfully');
        editMemberModal();
      })
      .catch(error => {
        console.log(error);
        if (error && error.response && error.response.data && error.response.data.message) {
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
      });
  }
}

    const steps = [
        {
            label: 'EMPLOYEE DETAILS',
            icon: <FaClipboardUser size={"30px"} color='#0B6BCB' />,
            validate: validateBasic,
            description: (
                <>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="First Name"
                            size="small"
                            type={'text'}
                            value={fname}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                },
                                maxlength: 15
                            }}
                            helperText={`${fname.length}/${15}`}
                            onChange={handleFirstNameChange}
                        />
                    </FormControl>
                    <br></br><br></br>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="Last Name"
                            size="small"
                            type={'text'}
                            value={lname}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                },
                                maxlength: 15
                            }}
                            helperText={`${lname.length}/${15}`}
                            onChange={handleLastNameChange}
                        />
                    </FormControl>
                    <br></br><br></br>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="Mobile"
                            size="small"
                            type={'text'}
                            value={mobile}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                }
                            }}
                            onChange={handleMobileChange}
                        />
                    </FormControl>
                    <br></br><br></br>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="Email"
                            size="small"
                            type={'email'}
                            value={email}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                }
                            }}
                            onChange={handleEmailChange}
                        />
                    </FormControl>

                    <br></br><br></br>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="Address"
                            multiline
                            rows={3}
                            size="small"
                            type={'text'}
                            value={address}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                },
                                maxlength: 300
                            }}
                            helperText={`${address.length}/${300}`}
                            onChange={handleAddressChange}
                        />
                    </FormControl>
                    <br></br><br></br>

                    <LocalizationProvider size="small" fullWidth dateAdapter={AdapterDateFns} >
                        <DesktopDatePicker
                            label="Date of Joining"
                            fullWidth
                            size="small"
                            value={doj}
                            disableFuture="true"
                            onChange={handleDojChange}
                            renderInput={(params) => <TextField size="small" fullWidth {...params} />}
                        /></LocalizationProvider>
                    <br></br>
                    <br></br>
                    <FormLabel style={{ fontSize: '0.9rem' }}>Years of experience during joining:</FormLabel>

                    <Stack direction={"row"} spacing={2} marginTop={"5px"}>

                        <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center', width: '90px' }}>
                            <TextField
                                id="standard-adornment-fname"
                                label="Years"
                                InputProps={{ inputProps: { min: 0 } }}
                                size="small"
                                type={'number'}
                                value={year}
                                onChange={handleYearChange}
                            />
                        </FormControl>
                        <FormControl style={{ width: '100px' }}>
                            <InputLabel id="demo-simple-select-label">Month</InputLabel>
                            <Select
                                variant="outlined"
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={month}
                                label="Month"
                                size='small'
                                onChange={handleMonthChange}
                            >
                                <MenuItem value={0}>0</MenuItem>
                                <MenuItem value={1}>1</MenuItem>
                                <MenuItem value={2}>2</MenuItem>
                                <MenuItem value={3}>3</MenuItem>
                                <MenuItem value={4}>4</MenuItem>
                                <MenuItem value={5}>5</MenuItem>
                                <MenuItem value={6}>6</MenuItem>
                                <MenuItem value={7}>7</MenuItem>
                                <MenuItem value={8}>8</MenuItem>
                                <MenuItem value={9}>9</MenuItem>
                                <MenuItem value={10}>10</MenuItem>
                                <MenuItem value={11}>11</MenuItem>
                            </Select>
                        </FormControl>
                    </Stack>
                    <br></br>
                    <Typography style={{ fontSize: '0.9rem' }}>Overall experience: {overallExperience}</Typography>
                    <br></br><br></br>
                    <FormControl fullWidth variant="standard">
                        <FormLabel id="demo-radio-buttons-group-label">Gender</FormLabel>
                        <Stack direction={"row"} spacing={2} marginLeft={"10px"}>
                            <RadioGroup
                                row
                                aria-labelledby="demo-radio-buttons-group-label"
                                defaultValue="female"
                                name="radio-buttons-group"
                                value={gender}
                                inputProps={{
                                    style: {
                                        fontSize: '0.9rem'
                                    }
                                }}
                                onChange={handleGenderChange}
                            >
                                <FormControlLabel value="Male" control={<Radio />} label="&nbsp;&nbsp;Male" />
                                <FormControlLabel value="Female" control={<Radio />} label="&nbsp;&nbsp;Female" />

                            </RadioGroup>
                        </Stack>
                    </FormControl>

                    <br></br><br></br>
                    <LocalizationProvider size="small" fullWidth dateAdapter={AdapterDateFns} >
                        <DesktopDatePicker
                            label="DOB"
                            fullWidth
                            size="small"
                            value={dob}
                            disableFuture="true"
                            onChange={handleDobChange}
                            renderInput={(params) => <TextField size="small" fullWidth {...params} />}
                        /></LocalizationProvider> &nbsp;&nbsp;&nbsp;
                    <br></br>
                </>
            ),
        },
        {
            label: 'MEMBER CREDENTIAL',
            icon: <RiLockPasswordFill size={"30px"} color='#0B6BCB' />,
            validate: validateCredential,
            description:
                <>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="Member ID"
                            size="small"
                            type={'text'}
                            disabled
                            value={memberId}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                }
                            }}
                            sx={{
                                '& .MuiInputBase-input.Mui-disabled': {
                                  color: '#000', 
                                  WebkitTextFillColor: '#000',
                                },
                                '& .MuiOutlinedInput-notchedOutline': {
                                  borderColor: '#000',
                                },
                              }}
                            onChange={handleIdChange}
                        />
                    </FormControl>
                    <br></br><br></br>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            disabled
                            label="Password"
                            size="small"
                            type={'password'}
                            value={password}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                }
                            }}
                            sx={{
                                '& .MuiInputBase-input.Mui-disabled': {
                                  color: '#000',
                                  WebkitTextFillColor: '#000',
                                },
                                '& .MuiOutlinedInput-notchedOutline': {
                                  borderColor: '#000',
                                },
                              }}
                            onChange={handlePasswordChange}
                        />
                    </FormControl>
                    <br></br><br></br>

                </>
        },
        {
            label: 'DESIGNATION & ROLE',
            icon: <HiUserGroup size={"30px"} color='#0B6BCB' />,
            validate: "",
            description:
                <>
                    <FormControl>
                        <FormLabel style={{ fontSize: '0.9rem' }}>Roles</FormLabel>
                        <RadioGroup
                            overlay
                            name="member"
                            value={role}
                            onChange={handleRoleChange}
                            orientation="horizontal"
                            sx={{ gap: 2 }}
                        >
                            {roleObj.map((num) => (
                                <Sheet
                                    component="label"
                                    key={num.value}
                                    variant="outlined"
                                    sx={{
                                        p: 2,
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'center',
                                        boxShadow: 'md',
                                        borderRadius: 'md',
                                    }}
                                >
                                    <Radio
                                        value={num.value}
                                        disabled={(selectedMemberId.toLowerCase())}
                                        checkedIcon={<CheckCircleRoundedIcon />}
                                        variant="soft"
                                        sx={{
                                            mb: 2,
                                        }}
                                    />

                                    <Typography level="body-sm" sx={{ mt: 1, fontSize: '0.9rem' }}>
                                        {num.label}
                                    </Typography>
                                </Sheet>
                            ))}
                        </RadioGroup>
                    </FormControl>
                    <br></br><br></br>
                    <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                        <TextField
                            id="standard-adornment-fname"
                            label="Designation"
                            size="small"
                            type={'text'}
                            value={designation}
                            inputProps={{
                                style: {
                                    fontSize: '0.9rem'
                                },
                                maxlength: 30
                            }}
                            helperText={`${designation.length}/${30}`}
                            onChange={handleDesignationChange}
                        />
                    </FormControl>

                    <br></br><br></br>



                </>
        },
    ];

    const theme = useTheme();
    const [activeStep, setActiveStep] = React.useState(0);
    const maxSteps = steps.length;

    const handleNext = () => {
        if (steps[activeStep].validate()) {
            setActiveStep((prevActiveStep) => prevActiveStep + 1);
        }

    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    return (
        <React.Fragment>
            <DialogContent>
                <Grid container >



                    <br></br><br></br>

                    <Box sx={{ flexGrow: 1, padding: '10px' }}>
                        <Paper
                            square
                            elevation={0}
                            sx={{
                                textAlign: 'center',
                                height: 50,
                                pl: 2,
                                bgcolor: 'background.default',
                            }}
                        >
                            <div style={{ textAlign: 'center', display: 'inline-flex' }} >{steps[activeStep].icon}</div>
                            <Typography style={{ textAlign: 'center', color: '#0B6BCB' }} class="font-bold">{steps[activeStep].label}</Typography>
                        </Paper><br></br>
                        <Divider /><br></br>
                        <Box class="font-bold">
                            {steps[activeStep].description}
                        </Box>
                        <MobileStepper
                            variant="progress"
                            steps={maxSteps}
                            position="static"
                            activeStep={activeStep}
                            nextButton={

                                activeStep !== maxSteps - 1 ?
                                    (
                                        <Button
                                            size="small"
                                            onClick={handleNext}
                                            disabled={activeStep === maxSteps - 1}
                                        >

                                            {theme.direction === 'rtl' ? (
                                                <KeyboardArrowLeft />
                                            ) : (
                                                <KeyboardArrowRight />
                                            )}
                                            NEXT
                                        </Button>
                                    ) : (
                                        <Button onClick={addNewMembers}>&nbsp;UPDATE</Button>

                                    )
                            }

                            backButton={
                                <Button size="small" onClick={handleBack} disabled={activeStep === 0}>
                                    {theme.direction === 'rtl' ? (
                                        <KeyboardArrowRight />
                                    ) : (
                                        <KeyboardArrowLeft />
                                    )}BACK
                                </Button>
                            }
                        />
                    </Box>





                </Grid>

            </DialogContent>
        </React.Fragment>
    );
}