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
} from "@material-ui/core";
import { withRouter } from "react-router-dom";
import classnames from "classnames";

// styles
import useStyles from "./styles";

// logo
import logo from "./images/logo.svg";
import google from "./images/google.svg";

// context
//import { findUser } from '../../apis/Api'
import { useUserDispatch, /*loginUser, createNewUser*/ } from "../../context/UserContext";

function Login(props) {
  var classes = useStyles();

  // global
  var userDispatch = useUserDispatch();

  // local
  var [isLoading, setIsLoading] = useState(false);
  var [loginError, setLoginError] = useState(null);
  var [signUpError, setSignupError] = useState(null);
  var [activeTabId, setActiveTabId] = useState(0);
  var [nameValue, setNameValue] = useState("");
  var [loginValue, setLoginValue] = useState("");
  var [passwordValue, setPasswordValue] = useState("");
  //var [isLoggin, setIsLoggin] = useState(false);

  async function findUser(loginValue,passwordValue) {
    try{
      return new Promise(resolve => {
        setTimeout(() => {
          resolve('token');
        }, 2000);
      })
    }catch(error){
      console.log(error)
    }
    /*return await fetch(this.BASE_URL, {
          method:'POST',
          headers: this.createHeaders(),
          body: JSON.stringify(item),
        });
      }*/
  }

  const createNewUser = async () => {
    setSignupError(false);
    setIsLoading(true)
    if (!!loginValue && !!passwordValue) {
      const user = await findUser(loginValue,passwordValue)
      if(!!user){
        setIsLoading(false)
        localStorage.setItem('id_token', 1)
        localStorage.setItem('id_session',1)
        localStorage.setItem('tracker_name', nameValue)
        userDispatch({ type: 'LOGIN_USER_SUCCESS' })
        props.history.push('/user/home')
      } else {
        setSignupError(true);
        setIsLoading(false);
      }
  }
}

  const loginUser = async () => {
    setLoginError(false);
    setIsLoading(true);
    if (!!loginValue && !!passwordValue) {
      const user = await findUser(loginValue,passwordValue)
      if(loginValue === 'user'){
        localStorage.setItem('id_token', 1)
        localStorage.setItem('tracker_name', 'Nacho Scocco')
        setIsLoading(false);
        userDispatch({ type: 'LOGIN_USER_SUCCESS' })
        props.history.push('/user/home')
      } else if( loginValue === 'admin'){
        localStorage.setItem('role', 1)
        localStorage.setItem('id_token', 1)
        localStorage.setItem('tracker_name', 'Jose Perez')
        setIsLoading(false);
        userDispatch({ type: 'LOGIN_ADMIN_SUCCESS' })
        props.history.push('/admin/home')
      } else {
        //userDispatch({ type: "LOGIN_FAILURE" });
        setLoginError(true);
        setIsLoading(false);
    }
  } 
}

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
              <Button size="large" className={classes.googleButton}>
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
                      loginUser()
                      //setIsLoggin(!isLoggin)
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
              <Typography variant="h2" className={classes.subGreeting}>
                Create your account
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
              <div className={classes.creatingButtonContainer}>
                {isLoading ? (
                  <CircularProgress size={26} />
                ) :
                  <Button
                    onClick={() =>{
                      createNewUser()
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

