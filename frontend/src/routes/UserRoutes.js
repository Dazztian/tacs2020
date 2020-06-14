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
import CrearListas from "../views/user/listas/CrearListas";
import EditarListas from "../views/user/listas/EditarListas";
import BorrarListas from "../views/user/listas/BorrarListas";
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
            {/*<Redirect from="/user" to="/user/home" />*/}
            <Switch>
              <Route path="/user/home" component={Dashboard} />
              <Route path="/user/CrearListas" component={CrearListas}  />
              <Route path="/user/EditarListas" component={EditarListas} />
              <Route path="/user/BorrarListas" component={BorrarListas} />
              <Route path="/user/liststats" />
              <Route path="/user/listas" />
              <Route path="/user/graficos" component={Graficos} />
              <Route path="/user/perfil" />
            </Switch>
          </div>
        </>
    </div>
  );
}

export default withRouter(LayoutUser);
