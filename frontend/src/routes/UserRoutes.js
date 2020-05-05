import React from "react";
import {
  Route,
  Switch,
  Redirect,
  withRouter,
} from "react-router-dom";
import classnames from "classnames";

// styles
import useStyles from "./styles/privateRoutesStyles";

// components
import Header from "../components/Header/Header";
import SidebarUser from "../components/Sidebar/SidebarUser";

// pages
import Dashboard from "../views/user/Dashboard";

// context
import { useLayoutState } from "../context/LayoutContext";

function LayoutUser(props) {
  var classes = useStyles();

  // global
  var layoutState = useLayoutState();

  return (
    <div className={classes.root}>
        <>
          <Header history={props.history} />
          <SidebarUser />
          <div
            className={classnames(classes.content, {
              [classes.contentShift]: layoutState.isSidebarOpened,
            })}
          >
            <div className={classes.fakeToolbar} />
            <Redirect from="/user" to="/user/dashboard" />
            <Switch>
              <Route path="/user/dashboard" component={Dashboard} />
              <Route path="/user/createlists" />
              <Route path="/user/editlists" />
              <Route path="/user/deletelists" />
              <Route path="/user/liststats" />
              <Route path="/user/perfil" />
            </Switch>
          </div>
        </>
    </div>
  );
}

export default withRouter(LayoutUser);
