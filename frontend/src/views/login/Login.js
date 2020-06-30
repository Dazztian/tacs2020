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
import { GoogleLogin } from 'react-google-login';

// styles
import useStyles from "./styles";

// logo
import logo from "./images/logo.svg";
import google from "./images/google.svg";

// context
import { useUserDispatch } from "../../context/UserContext";
import Api from "../../apis/Api"

const api = new Api()
let countryList = []
function Login(props) {
  var classes = useStyles();
  // global
  var userDispatch = useUserDispatch();
  //var [countryList, setCountryList] = useState([])
  var [isLoading, setIsLoading] = useState(false);
  var [loginError, setLoginError] = useState(null);
  var [signUpError, setSignupError] = useState(null);
  var [activeTabId, setActiveTabId] = useState(0);
  var [nameValue, setNameValue] = useState("");
  var [loginValue, setLoginValue] = useState("");
  var [passwordValue, setPasswordValue] = useState("");
  var [countryValue, setCountryValue] = useState("");

  const responseGoogle = (response) => {
    console.log(response);
  }
  
  const handleLoginWithGoogle = async (response) => {
    //const userCountrIso = countryList.filter(country => country.name===response.country)[0].iso2
    const res = await api.loginUserWithGoogle(response.tokenId)
    //const data = await api.loginUserWithGoogle(response.tokenId,response.mail,response.name,userCountrIso)
    if(res.ok) {
        let data = await res.json()
        console.log(data)
          const { user, token } = data;
          localStorage.setItem('tracker_id_token', token)
          localStorage.setItem('tracker_id_session',user.id)
          localStorage.setItem('tracker_tracker_name', user.name)
          setIsLoading(false);
          if(!user.isAdmin){ 
            userDispatch({ type: 'LOGIN_USER_SUCCESS' })
            props.history.push('/user/home')
          } else {
            localStorage.setItem('role', user.isAdmin)
            userDispatch({ type: 'LOGIN_ADMIN_SUCCESS' })
            props.history.push('/admin/home')
          }
    } else {
      
    }
  }

  const handleCreateNewUser = async (userDispatch,nameValue,loginValue,passwordValue,countryISo,history,setIsLoading,setSignupError) => {
    setSignupError(false);
    setIsLoading(true)
      const res = await api.createUser(nameValue,loginValue,passwordValue,countryISo)
      if(res.ok) {
        const {user, token} = await res.json()
          localStorage.setItem('tracker_id_token', token)
          localStorage.setItem('tracker_id_session',user.id)
          localStorage.setItem('tracker_name', user.name)
          localStorage.setItem('tracker_country_Iso',countryISo)
          userDispatch({ type: 'LOGIN_USER_SUCCESS' })
          history.push('/user/home')
      } else {
        setSignupError(true);
        setIsLoading(false);
      }
  }

  const handleLoginUser = async (userDispatch,loginValue,passwordValue,history,setIsLoading,setLoginError) => {
    setLoginError(false)
    setIsLoading(true)
    if (!!loginValue && !!passwordValue) {
      //tener en cuenta q devuelve la res, esa hay q parsearla a json
      const res = await api.loginUser(loginValue,passwordValue)
      if(res.ok) {
        const data = await res.json()
        const { user, token } = data;
        user["id"] 
        ? localStorage.setItem('tracker_id_session',user["id"])
        : localStorage.setItem('tracker_id_session',user.id)
        localStorage.setItem('tracker_id_token', token)
        localStorage.setItem('tracker_name', user.name)
        localStorage.setItem('tracker_country_Iso',null)
        setIsLoading(false);
        if(!user.isAdmin){ 
          userDispatch({ type: 'LOGIN_USER_SUCCESS' })
          history.push('/user/home')
        } else {
          localStorage.setItem('role', user.isAdmin)
          userDispatch({ type: 'LOGIN_ADMIN_SUCCESS' })
          history.push('/admin/home')
        }
      } else { //este else va por el res.ok
        setLoginError(true);
        setIsLoading(false);
      }
    } 
  }

  async function fetchCountries(){
      const res = await api.getCountryList()
      if(res.ok){
        countryList = await res.json()        
      } else {
        console.log(res.errorMessage)
      }
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
              <GoogleLogin
                clientId="850038158644-32c2v3i19hur7v95ttbnlaq5qi49b85e.apps.googleusercontent.com"
                render={renderProps => (
                  <Button size="large" className={classes.googleButton} onClick={renderProps.onClick}>
                    <img src={google} alt="google" className={classes.googleIcon} />
                       &nbsp;Sign in with Google
                  </Button>
                )}
                onSuccess={handleLoginWithGoogle}
                onFailure={responseGoogle}
                cookiePolicy={'single_host_origin'}
              />
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
                  <Grid container justify="center"> 
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
                    fullWidth
                  >
                    Login
                  </Button>
                  </Grid>
                )}
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
                value={countryValue}
                onChange={e => setCountryValue(e.target.value)}
                margin="normal"
                placeholder="Country"
                type="Country"
                helperText="Select your country"
                fullWidth
              >
                {
                countryList.map((country) => (
                  <MenuItem key={country.iso2} value={country.iso2}>
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
                        countryValue,
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
              <GoogleLogin
                clientId="850038158644-32c2v3i19hur7v95ttbnlaq5qi49b85e.apps.googleusercontent.com"
                render={renderProps => (
                  <Button size="large" className={classes.googleButton} onClick={renderProps.onClick}>
                    <img src={google} alt="google" className={classes.googleIcon} />
                       &nbsp;Sign in with Google
                  </Button>
                )}
                onSuccess={handleLoginWithGoogle}
                onFailure={responseGoogle}
                cookiePolicy={'single_host_origin'}
              />
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

