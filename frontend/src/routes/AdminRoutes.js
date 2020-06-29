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
import SidebarAdmin from "../components/Sidebar/SidebarAdmin";

// pages
import Dashboard from "../views/admin/dashboard/Dashboard";
import PaisEnComun from "../views/admin/paisencomun/PaisEnComun";
import Countries from "../views/admin/countriesandusers/CountriesAndUsers";
import CantUsuariosInteresados from "../views/admin/cantusuariosinteresados/CantUsuariosInteresados";
import ListasRegistradas from "../views/admin/listasregistradas/ListasRegistradas";

// context
import { useLayoutState } from "../context/LayoutContext";

function LayoutAdmin(props) {
  var classes = useStyles();

  // global
  var layoutState = useLayoutState();

  return (
    <div className={classes.root}>
        <>
          <Header history={props.history} />
          <SidebarAdmin />
          <div
            className={classnames(classes.content, {
              [classes.contentShift]: layoutState.isSidebarOpened,
            })}
          >
            <div className={classes.fakeToolbar} />
            <Redirect from="/admin" to="/admin/home" />
            <Switch>
            <Route path="/admin/home" component={Dashboard}/>
              <Route path="/admin/usersearch"/>
              <Route path="/admin/countries" component={Countries}/>
              {/*<Route path="/admin/countries" component={PaisEnComun}/>*/}
              <Route path="/admin/interestedUsers" component={CantUsuariosInteresados}/>
              <Route path="/admin/registeredLists" component={ListasRegistradas}/>
            </Switch>
          </div>
        </>
    </div>
  );
}

export default withRouter(LayoutAdmin);