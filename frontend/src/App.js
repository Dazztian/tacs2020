import React from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Redirect,
  useHistory,
  useLocation
} from "react-router-dom";

// Rutas de
import Layout from "./routes/Layout";
import LayoutUser from "./routes/UserRoutes";
import LayoutAdmin from "./routes/AdminRoutes";

// Vistas publicas
import Error from "./views/error/Error";
import Login from "./views/login/Login";

// context
import { useUserState } from "./context/UserContext";

export default function App() {
  // global
  var { isAuthenticated, isAdmin } = useUserState();

  return (
    <Router>
      <Switch>
        <PublicRoute exact path="/" />
        <Route path="/app" component={Layout} />
        <PrivateRouteUser path="/user" />
        <PrivateRouteAdmin path="/admin" />
        <PublicRoute path="/login" />
        <Route component={Error} />
      </Switch>
    </Router>
  );                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
  // #######################################################################

  function PrivateRouteAdmin({ children, ...rest }) {
    return (
      <Route
        {...rest}
        render={props =>
          isAuthenticated ? (
            isAdmin ? (
              React.createElement(LayoutAdmin, props)
            ) : (
              <Redirect
                to={{
                  pathname: "/user",
                  state: { from: props.location }
                }}
              />
            )
          ) : (
            <Redirect
              to={{
                pathname: "/login",
                state: { from: props.location }
              }}
            />
          )
        }
      />
    );
  }

  function PrivateRouteUser({ children, ...rest }) {
    return (
      <Route
        {...rest}
        render={props =>
          isAuthenticated ? (
            isAdmin ? (
              <Redirect
                to={{
                  pathname: "/admin",
                  state: { from: props.location }
                }}
              />
            ) : (
              React.createElement(LayoutUser, props)
            )
          ) : (
            <Redirect
              to={{
                pathname: "/login",
                state: { from: props.location }
              }}
            />
          )
        }
      />
    );
  }

  function PublicRoute({ children, ...rest }) {
    return (
      <Route
        {...rest}
        render={props =>
          !isAuthenticated ? (
            React.createElement(Login, props)
          ) : (
            isAdmin ? (
              <Redirect
              to={{
                pathname: "/admin",
                state: { from: props.location }
              }}
            />
            ) : (
              <Redirect
              to={{
                pathname: "/user",
                state: { from: props.location }
              }}
            />
            )
          )
        }
      />
    );
  }
}
