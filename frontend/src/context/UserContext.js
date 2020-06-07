import React from "react";
var UserStateContext = React.createContext();
var UserDispatchContext = React.createContext();

function userReducer(state, action) {
  switch (action.type) {
    case "LOGIN_USER_SUCCESS": 
      return { ...state, isAuthenticated: true, isAdmin: false };
    case "LOGIN_ADMIN_SUCCESS": 
      return { ...state, isAuthenticated: true, isAdmin: true };
    case "SIGN_OUT_SUCCESS":
      return { ...state, isAuthenticated: false, isAdmin: false };
    case "LOGIN_FAILURE":
      return { ...state };
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
}

function UserProvider({ children }) {

  var [state, dispatch] = React.useReducer(userReducer, {
    isAuthenticated: !!localStorage.getItem("id_token"),
    isAdmin: !!localStorage.getItem("role"),
  });

  return (
    <UserStateContext.Provider value={state}>
      <UserDispatchContext.Provider value={dispatch}>
        {children}
      </UserDispatchContext.Provider>
    </UserStateContext.Provider>
  );
}

function useUserState() {
  var context = React.useContext(UserStateContext);
  if (context === undefined) {
    throw new Error("useUserState must be used within a UserProvider");
  }
  return context;
}

function useUserDispatch() {
  var context = React.useContext(UserDispatchContext);
  if (context === undefined) {
    throw new Error("useUserDispatch must be used within a UserProvider");
  }
  return context;
}

export { UserProvider, useUserState, useUserDispatch, loginUser, createNewUser, signOut };

// ###########################################################

const loginUser = async (dispatch, login, password, history, setIsLoading, setError) => {
  setError(false);
  if (!!login && !!password) {
    //seria dps del fetch
    setIsLoading(false);
    //localStorage.setItem('tracker_country', 'Argentina')
    if(login === 'user'){
      localStorage.setItem('id_token', 1)
      localStorage.setItem('tracker_name', 'Nacho Scocco')
      setTimeout(() => {
        dispatch({ type: 'LOGIN_USER_SUCCESS' })
        history.push('/user/home')
      }, 2000);
    } else if( login === 'admin'){
      localStorage.setItem('role', 1)
      localStorage.setItem('id_token', 1)
      localStorage.setItem('tracker_name', 'Jose Perez')
      setTimeout(() => {
        dispatch({ type: 'LOGIN_ADMIN_SUCCESS' })
        history.push('/admin/home')
      }, 2000);
    }
    setError(null)
  } else {
    dispatch({ type: "LOGIN_FAILURE" });
    setError(true);
  }
}

const createNewUser = async (dispatch, nameValue, login, password, history, setIsLoading, setError) => {
  setError(false);

  if (!!login && !!password) {
    setError(null)
    setIsLoading(false)
      localStorage.setItem('id_token', 1)
      //localStorage.setItem('tracker_country', 'Argentina')
      localStorage.setItem('tracker_name', nameValue)
      setTimeout(() => {
        dispatch({ type: 'LOGIN_USER_SUCCESS' })
        history.push('/user/home')
      }, 2000);
    } else {
      dispatch({ type: "LOGIN_FAILURE" });
      setError(true);
    }
  }

function signOut(dispatch, history) {
  localStorage.removeItem("id_token");
  localStorage.removeItem('tracker_name');
  localStorage.removeItem('tracker_country');
  localStorage.removeItem("role");
  dispatch({ type: "SIGN_OUT_SUCCESS" });
  history.push("/login");
}


/*

async function loginUser(dispatch, login, password, history, setIsLoading, setError) {
  setError(false);
  setIsLoading(true);
  
  try{
    if (!!name && !!password && !!mail) {
      createNewUser(name,mail,password)
      .then(res => {
        if (res.ok) {
          return res.json();
        } else if(res.status === 400){
          // eslint-disable-next-line no-throw-literal
          throw "Mail already used";
        } else {
          throw res;
        }
      }).then( ({user, token}) => {
          localStorage.setItem('tracker_id_token', token)
          localStorage.setItem('tracker_name', user.name)
          localStorage.setItem('tracker_country', user.country)
          setError(null)
          setIsLoading(false)
          dispatch({ type: 'LOGIN_USER_SUCCESS' })
          history.push('/user/home')
        })
      } else {
        // eslint-disable-next-line no-throw-literal
        throw "Missing values";
      }
    } catch(error){
      console.log(error)
      dispatch({ type: "LOGIN_FAILURE" });
      setError(true);
      setIsLoading(false);
    }
  }

function signupUser(dispatch, name, password, mail, /*country, history, setIsLoading, setError) {
  setError(false);
  setIsLoading(true);
  
  try{
    if (!!name && !!password && !!mail) {
      createNewUser(name,mail,password)
      .then(res => {
        if (res.ok) {
          return res.json();
        } else if(res.status === 400){
          // eslint-disable-next-line no-throw-literal
          throw "Mail already used";
        } else {
          throw res;
        }
      }).then( ({user, token}) => {
          localStorage.setItem('tracker_id_token', token)
          localStorage.setItem('tracker_name', user.name)
          localStorage.setItem('tracker_country', user.country)
          setError(null)
          setIsLoading(false)
          dispatch({ type: 'LOGIN_USER_SUCCESS' })
          history.push('/user/home')
        })
      } else {
        // eslint-disable-next-line no-throw-literal
        throw "Missing values";
      }
    } catch(error){
      console.log(error)
      dispatch({ type: "LOGIN_FAILURE" });
      setError(true);
      setIsLoading(false);
    }
  }

function signOut(dispatch, history) {
  localStorage.removeItem("id_token");
  localStorage.removeItem('tracker_name')
  localStorage.removeItem('tracker_country')
  dispatch({ type: "SIGN_OUT_SUCCESS" });
  history.push("/");
}

let findUser = async (user,password)=>{
  try{
  let res = await fetch("http://localhost:8080/api/login",{
      method:"POST",
      headers:{
          'Content-Type': 'application/json'
      },
      data:{
        'email': user,
        'password': password
      }
  })
  return res;
  }
  catch(err) {
      console.log(err)
      window.alert(err)
  }
}

let createNewUser = async (name,mail,password,country)=>{
  try{
  let res = await fetch("http://localhost:8080/api/signup",{
      method:"POST",
      headers:{
          'Content-Type': 'application/json'
      },
      body:{
        'name': name,
        'email': mail,
        'password': password,
        //'country': country
      }
  })
  return res;
  }
  catch(err) {
      console.log(err)
      window.alert(err)
  }
}*/
