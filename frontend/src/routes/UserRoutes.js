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
import Stats from "../views/user/stats/Stats";
import BorrarListas from "../views/user/listas/BorrarListas";
import CrearListas from "../views/user/listas/CrearListas";
import EditarListas from "../views/user/listas/EditarListas";

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

            <Redirect from="/user" to="/user/home" />

            <Switch>
              <Route path="/user/home" component={Dashboard} />
              <Route path="/user/createlists" component={CrearListas}/>
              <Route path="/user/editlists" component={EditarListas}/>
              <Route path="/user/deletelists" component={BorrarListas}/>
              <Route path="/user/liststats" component={Stats}/>
              <Route path="/user/perfil" />
            </Switch>
          </div>
        </>
    </div>
  );
}

export default withRouter(LayoutUser);