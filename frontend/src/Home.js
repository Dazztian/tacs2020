/*import React, { Component } from 'react';
import './App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';

class Home extends Component {

  render() {
    if (this.props.authenticated === null) {
      return <p>Loading...</p>;
    }

    return (
      <div className="app">
        {this.props.navbar}
        <Container fluid>
          { this.props.authenticated ?
            <div>
              <p>Welcome, {this.props.user.name}</p>
              <Button color="secondary">
                <Link className="app-link" to="/coffee-shops">Manage Coffee Shops</Link>
              </Button>
            </div> :
            <div>
              <p>Please log in to manage coffee shops.</p>
              <Button color="secondary" disabled={true}>
                Manage Coffee Shops
              </Button>
            </div>
          }
        </Container>
      </div>
    );
  }
}

export default Home;*/
import React from 'react';
import {StyleSheet, Text,View} from "react-native-web";
import { Card} from 'react-native-paper'

function Home(){
  return(
      <Card>
      <Text>ESTA ES UNA PAGINA DE PRUEBA</Text>
  </Card>

)
}

const styles= StyleSheet.create({
  container:{
    flex:1,
    backgroundColor:'#fff',
    alignItems:'center',
    justifyContent:'center'
  }

})

export default Home