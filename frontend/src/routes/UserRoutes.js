import React from "react";
import {
  Route,
  Switch,
  Redirect,
  withRouter,
} from "react-router-dom";
import classnames from "classnames";

// styles
import useStyles from "./styles";

// components
import Header from "../components/Header/Header";
import SidebarUser from "../components/Sidebar/SidebarUser";

// pages
import Dashboard from "../views/user/dashboard/Dashboard";
import Listas from "../views/user/listas/Listas";
import Graficos from "../views/user/graficos/Graficos";

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
            {/*<Redirect from="/user" to="/user/dashboard" />*/}
            <Switch>
              <Route path="/user/dashboard" component={Dashboard} />
              <Route path="/user/createlists" />
              <Route path="/user/editlists" />
              <Route path="/user/deletelists" />
              <Route path="/user/liststats" />
              <Route path="/user/listas" component={Listas} />
              <Route path="/user/graficos" component={Graficos} />
              <Route path="/user/perfil" />
            </Switch>
          </div>
        </>
    </div>
  );
}

export default withRouter(LayoutUser);
