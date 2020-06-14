import React, {useState, useEffect} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import Button from '@material-ui/core/Button';
import { Grid, Container,TextField, } from '@material-ui/core';

import MUIDataTable from "mui-datatables";
import ReactDOM from "react-dom";

import PageTitle from "../../../components/PageTitle/PageTitle";

import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { FixedSizeList } from 'react-window';

const EditarListas = ()=>{
    let sec;


    const [unArray,setUnArray] = useState([{name:null,paises:[]}])
    const [count,setCount] = useState(0)
    const [loading,setLoading] = useState(false)

    const [unArrayListasDePaises,setunArrayListasDePaises] = useState([{}])

    /*----------------------------PARA CORRERLO ONLINE--------------------------------
    const id_user= "5ee3a85273699c3db95bd3e1"
    const token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTNhODUyNzM2OTljM2RiOTViZDNlMSIsImV4cCI6MTU5MjAxNDA2Nn0.Ux5REf5nKHTTgkDAJrwjYnJbsbAVGnnzf_EHZlf6xDM"
    const BASE_URL = 'https://eca5bc85109d.ngrok.io';
    */

    /*----------------------------PARA CORRERLO LOCAL--------------------------------*/
    const id_user= "5ee63779ad140055ea0d76ba"
    const BASE_URL = 'http://localhost:8080';
    const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTYzNzc5YWQxNDAwNTVlYTBkNzZiYSIsImV4cCI6MTU5MjE4MTc4NX0.fiFuq27BhdjLLJwoo8bHi1oJV60kMum59wvfQfoeO1g"

    const obtenerListaDePaisesXUsuario = async ()=>{
        try{
            let res = await fetch( BASE_URL +"/api/user/"+ id_user + "/lists" ,{
            method:"get",
            headers:{
                'Accept': 'application/json',
                'Authorization' : 'Bearer '+ token
                }
            })
            let elemento = await res.json();

            let promArray = elemento.map( item=>{ return {name:item.name, id:item.id,
                 paises:item.countries.map(pais => [pais]) } })

            let resultArray = await Promise.all(promArray)

            setUnArray(resultArray)  
           

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }

    }

    const idLista="5ee63e9ead140055ea0d76be"
    const updateLista = async (unIdLista, nuevoNombre, nuevosPaises)=>{
        try{

            let res = await fetch( BASE_URL +"/api/user/"+ id_user + "/lists/"+unIdLista ,{
            method:"put",
            headers:{
              'Accept': 'application/json',
              'Content-Type': 'application/json',
              'Authorization' : 'Bearer '+ token
                },
                body:JSON.stringify({
                    "name": nuevoNombre,
                    "countries":nuevosPaises
                })
            })
            window.alert("Lista: "+nuevoNombre+ " modificada exitosamente")
        }
        catch(err) {
            console.log("ERROR AL MODIFICAR LISTA")
            console.log(err)
            window.alert(err)
        }

    }

    const obtenerTodosLosPaises=  async ()=>{
      try{
          let res = await fetch( BASE_URL +"/api/user/"+ id_user + "/lists" ,{
          method:"get",
          headers:{
              'Accept': 'application/json',
              'Authorization' : 'Bearer '+ token
              }
          })
          let elemento = await res.json();

          let promArray = elemento.map( item=>{ return {name:item.name, id:item.id,
               paises:item.countries.map(pais => [pais]) } })

          let resultArray = await Promise.all(promArray)

          setUnArray(resultArray)  
         

      }
      catch(err) {
          console.log(err)
          window.alert(err)
      }

  }


    /*-----------------ESTE CODIGO ES EL QUE GENERA EL SELECT LIST-----------------*/
    const Column = ({ index, style }) => (
      <ListItem button style={style} key={index} 
       onClick={()=>obtenerTodosLosPaises()}
      >
      <ListItemText primary={unArrayListasDePaises[index].name} 
                    secondary={unArrayListasDePaises[index].id}//Queda pendiente ver como ocultar el id
      /> {/*obtener lista por id*/}
      </ListItem>
    );
     
    const Example = () => (
      <FixedSizeList
        height={125}
        itemCount={unArrayListasDePaises.length}
        itemSize={150}
        layout="horizontal"
        width={450}
      >
        {Column}
      </FixedSizeList>
    )
    /*-----------------ACA TERMINA EL CODIGO  QUE GENERA EL SELECT LIST---------------*/


    useEffect(() => {
        obtenerListaDePaisesXUsuario()
    }, [count]); 

    //var classes = useStyles();

    return(
            <>
            <PageTitle title="Listas de paises a EDITAR, escoja la lista a editar" />            

            {unArray.map(elemento =>{ return [
              
            <Grid container spacing={4} >
            {/*<Grid item lg={10} md={4} sm={6}>*/}
            <Grid item lg={12}  sm={6} >

            <Grid   
              item lg={12} md={1} sm={1}      
              container
              spacing={1}
              alignItems="center"
              justify="left"
            >
            <Grid item xs={3} md={3} >
              <TextField
                id="filled-number"
                label="Nuevo nombre de la lista"
                //type="number"
                margin='dense'
                size='small'
                fullWidth={false}
                inputProps={
                  {step: 1,}
                }
                onChange={(e) => { sec=e.target.value } }
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
          <Grid item xs={3} md={3}>
            <Button  variant="contained" color="primary" 
            onClick={(e)=>updateLista(elemento.id, sec, flatenizarNombrePaises(elemento)   ) }
            >    
              Cambiar Nombre
            </Button>
          </Grid>
          <Grid item xs={3}>
            <Example />{/*Componente que te permite elegir la lista a mostrar */}
          </Grid> 
          <Grid item xs={3}   justify="flex-end">
            <Button  variant="contained" color="secondary"
            onClick={()=>console.log("boton para agregar paises")}> Agregar paises
            </Button>
          </Grid>
        </Grid>

            <MUIDataTable
            title={ elemento.name}
            data={elemento.paises}
            columns={["paises"]}
            options={{
                filter: true,
                selectableRows: 'multiple',
                selectableRowsOnClick: true,
                filterType: 'dropdown',
                responsive: 'stacked',
                rowsPerPage: 10,
            }
            }
            />
            </Grid>
            </Grid>
            ]
             })
            
            }
            </>
        )
}   


export default EditarListas 

function flatenizarNombrePaises(elemento) {
  return [].concat.apply([], elemento.paises);
}
