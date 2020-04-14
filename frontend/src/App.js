import React from 'react';
import {StyleSheet, Text,View} from "react-native-web";
import Home from './screens/Home'
import CreateUser from './screens/CreateUser'
import Login from './screens/Login'
import { BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";

export default function App(){
    return(
        <Router>
            <div className="container">
                <h1>BARRA DE NAVEGACION...</h1>
                <hr/>
                <Switch>
                    <Route>
                        <CreateUser/>
                    </Route>
                </Switch>
            </div>
        </Router>

    )
}

const styles= StyleSheet.create({
    container:{
        flex:1,
        backgroundColor:'#fff',
    }

})