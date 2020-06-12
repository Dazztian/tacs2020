import React, { useState, useEffect } from "react";
import {
  Grid,
  CircularProgress,
  Typography,
  Button,
  Tabs,
  Tab,
  TextField,
  Fade,
  MenuItem 
} from "@material-ui/core";
import { withRouter } from "react-router-dom";
import classnames from "classnames";

// styles
import useStyles from "./styles";

// logo
import logo from "./images/logo.svg";
import google from "./images/google.svg";

// context
import { useUserDispatch } from "../../context/UserContext";
import { loginUser, createUser } from "../../apis/PublicApi"
import Api from "../../apis/Api"
import { getCountry } from "../../apis/GeolocationApi";

const api = new Api()
let countryList = []

function Login(props) {
  var classes = useStyles();
  // global
  var userDispatch = useUserDispatch();
  
  var [isLoading, setIsLoading] = useState(false);
  var [loginError, setLoginError] = useState(null);
  var [signUpError, setSignupError] = useState(null);
  var [activeTabId, setActiveTabId] = useState(0);
  var [nameValue, setNameValue] = useState("");
  var [loginValue, setLoginValue] = useState("");
  var [passwordValue, setPasswordValue] = useState("");
  var [countryValue, setCountryValue] = useState("");
  


  const handleLoginWithGoogle = async () => {
    
  }

  const handleCreateNewUser = async (userDispatch,nameValue,loginValue,passwordValue,history,setIsLoading,setSignupError) => {
    setSignupError(false);
    setIsLoading(true)
    if (!!loginValue && !!passwordValue) {
      const res = await api.createUser(nameValue,loginValue,passwordValue)
      if(true/*res.ok*/) {
        //const {user, token} = await res.json()
        const {user, token} = res;
        if(!!token){
          localStorage.setItem('id_token', token)
          localStorage.setItem('id_session',user._id)
          localStorage.setItem('tracker_name', user.name)
          localStorage.setItem('countriesList',countryList)
          userDispatch({ type: 'LOGIN_USER_SUCCESS' })
          history.push('/user/home')
        } 
      } else {
        setSignupError(true);
        setIsLoading(false);
      }
    }
  }

  const handleLoginUser = async (userDispatch,loginValue,passwordValue,history,setIsLoading,setLoginError) => {
    setLoginError(false);
    setIsLoading(true);
    if (!!loginValue && !!passwordValue) {
      //tener en cuenta q devuelve la res, esa hay q parsearla a json
      const res = await api.loginUser(loginValue,passwordValue)
      if(true/*res.ok*/) {
        //const {user, token} = await res.json()
        const {user,token} = res;
        if(loginValue !== 'admin' /*!user.isAdmin*/){ 
          localStorage.setItem('id_session',user._id)
          localStorage.setItem('id_token', token)
          localStorage.setItem('tracker_name', user.name)
          localStorage.setItem('countriesList',countryList)
          userDispatch({ type: 'LOGIN_USER_SUCCESS' })
          history.push('/user/home')
        } else {
          localStorage.setItem('role', 1)
          localStorage.setItem('id_token', 1)
          localStorage.setItem('tracker_name', 'Jose Perez')
          //setIsLoading(false);
          userDispatch({ type: 'LOGIN_ADMIN_SUCCESS' })
          history.push('/admin/home')
        }
      } else { //este else va por el res.ok
        //userDispatch({ type: "LOGIN_FAILURE" });
        setLoginError(true);
        setIsLoading(false);
      }
    } 
  }

  async function fetchCountries(){
      //const res = await api.getCountryList()
      //if(true/*res.ok*/){
      //countryList = await res.json()
      countryList = await api.getCountryList()
      /*console.log(countryList)
      } else {
        console.log(res.errorMessage)
      }*/
  }

  useEffect(() => { //tiene que haber un useEffect por cada variable de estado de chart a modificar
    fetchCountries()
  },[]);

  return (
    <Grid container className={classes.container}>
      <div className={classes.logotypeContainer}>
      <p></p><Typography variant="h1" className={classes.greeting}> 
        UTN FRBA
      </Typography><p/>
      <img src={logo} alt="logo" className={classes.logotypeImage} />
        <Typography variant="h1" className={classes.greeting}>
          TACS    
        </Typography>
      </div>
      <div className={classes.formContainer}>
        <div className={classes.form}>
          <Tabs
            value={activeTabId}
            onChange={(e, id) => {setActiveTabId(id); setLoginError(false); setSignupError(false)}} 
            indicatorColor="primary"
            textColor="primary"
            centered
          >
            <Tab label="Login" classes={{ root: classes.tab }} />
            <Tab label="New User" classes={{ root: classes.tab }} />
          </Tabs>
          {activeTabId === 0 && (
            <React.Fragment>
              <Typography variant="h1" className={classes.greeting}>
                Covid-19 Tracker
              </Typography>
              <Button size="large" className={classes.googleButton} onClick={() => handleLoginWithGoogle()}>
                <img src={google} alt="google" className={classes.googleIcon} />
                &nbsp;Sign in with Google
              </Button>
              <div className={classes.formDividerContainer}>
                <div className={classes.formDivider} />
                <Typography className={classes.formDividerWord}>or</Typography>
                <div className={classes.formDivider} />
              </div>
              <Fade in={loginError} timeout={500}>
                <Typography color='error' className={classes.errorMessage}>
                    User or password incorrect.
                </Typography>
              </Fade>
              <TextField
                id="email"
                InputProps={{
                  classes: {
                    underline: classes.textFieldUnderline,
                    input: classes.textField,
                  },
                }}
                value={loginValue}
                onChange={e => setLoginValue(e.target.value)}
                margin="normal"
                placeholder="Email Adress"
                type="email"
                fullWidth
              />
              <TextField
                id="password"
                InputProps={{
                  classes: {
                    underline: classes.textFieldUnderline,
                    input: classes.textField,
                  },
                }}
                value={passwordValue}
                onChange={e => setPasswordValue(e.target.value)}
                margin="normal"
                placeholder="Password"
                type="password"
                fullWidth
              />
              <div className={classes.formButtons}>
                {isLoading ? (
                  <CircularProgress size={26} className={classes.loginLoader} />
                ) : (
                  <Button
                    disabled={
                      loginValue.length === 0 || passwordValue.length === 0
                    }
                    onClick={() => {
                      handleLoginUser(                        
                        userDispatch,
                        loginValue,
                        passwordValue,
                        props.history,
                        setIsLoading,
                        setLoginError
                      )
                    }
                    }
                    variant="contained"
                    color="primary"
                    size="large"
                  >
                    Login
                  </Button>
                )}
                <Button
                  color="primary"
                  size="large"
                  className={classes.forgetButton}
                >
                  Forget Password
                </Button>
              </div>
            </React.Fragment>
          )}
          {activeTabId === 1 && (
            <React.Fragment>
              <Typography variant="h1" className={classes.greeting}>
                Welcome!
              </Typography>
              <Fade in={signUpError} timeout={500}>
                <Typography color='error' className={classes.errorMessage}>
                    Email already used.
                </Typography>
              </Fade>
              <TextField
                id="name"
                InputProps={{
                  classes: {
                    underline: classes.textFieldUnderline,
                    input: classes.textField,
                  },
                }}
                value={nameValue}
                onChange={e => setNameValue(e.target.value)}
                margin="normal"
                placeholder="Full Name"
                type="text"
                fullWidth
              />
              <TextField
                id="email"
                InputProps={{
                  classes: {
                    underline: classes.textFieldUnderline,
                    input: classes.textField,
                  },
                }}
                value={loginValue}
                onChange={e => setLoginValue(e.target.value)}
                margin="normal"
                placeholder="Email Adress"
                type="email"
                fullWidth
              />
              <TextField
                id="password"
                InputProps={{
                  classes: {
                    underline: classes.textFieldUnderline,
                    input: classes.textField,
                  },
                }}
                value={passwordValue}
                onChange={e => setPasswordValue(e.target.value)}
                margin="normal"
                placeholder="Password"
                type="password"
                fullWidth
              />
              <TextField
                id="country"
                InputProps={{
                  classes: {
                    underline: classes.textFieldUnderline,
                    input: classes.textField,
                  },
                }}
                select
                value={countryValue.country}
                onChange={e => setCountryValue({'country': e.target.value,'iso2': e.target.key})}
                margin="normal"
                placeholder="Country"
                type="Country"
                helperText="Select your country"
                fullWidth
              >
                {
                countryList.map((country) => (
                  <MenuItem key={country.iso2} value={country.name}>
                    {country.name}
                  </MenuItem>
                ))}
              </TextField>
              <div className={classes.creatingButtonContainer}>
                {isLoading ? (
                  <CircularProgress size={26} />
                ) :
                  <Button
                    onClick={() =>{
                      handleCreateNewUser(
                        userDispatch,
                        nameValue,
                        loginValue,
                        passwordValue,
                        props.history,
                        setIsLoading,
                        setSignupError)
                      /*createNewUser(
                        userDispatch,
                        nameValue,
                        loginValue,
                        passwordValue,
                        props.history,
                        setIsLoading,
                        setError
                      )*/
                    //setIsLoading(true)
                    }
                    }
                    disabled={
                      loginValue.length === 0 ||
                      passwordValue.length === 0 ||
                      nameValue.length === 0
                    }
                    size="large"
                    variant="contained"
                    color="primary"
                    fullWidth
                    className={classes.createAccountButton}
                  >
                    Create your account
                  </Button>
                }
              </div>
              <div className={classes.formDividerContainer}>
                <div className={classes.formDivider} />
                <Typography className={classes.formDividerWord}>or</Typography>
                <div className={classes.formDivider} />
              </div>
              <Button
                onClick={() => handleLoginWithGoogle()}
                size="large"
                className={classnames(
                  classes.googleButton,
                  classes.googleButtonCreating,
                )}
              >
                <img src={google} alt="google" className={classes.googleIcon} />
                &nbsp;Sign in with Google
              </Button>
            </React.Fragment>
          )}
        </div>
        <Typography color="primary" className={classes.copyright}>
        
        </Typography>
      </div>
    </Grid>
    );
  }


export default withRouter(Login);

