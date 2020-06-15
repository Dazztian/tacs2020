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

    const [unArrayTodosLosPaises,setUnArrayTodosLosPaises] = useState([])
    const [unArray,setUnArray] = useState([{name:null,paises:[]}])
    const [count,setCount] = useState(0)
    const [loading,setLoading] = useState(false)

    var listarray = new Array();
    const state = { rowsSelected: [] };
    let sec;

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

    const obtenerListaDePaises = async ()=>{
      try{
      let res = await fetch("http://localhost:8080/api/countries",{
          method:"GET",
          headers:{
              'Accept': 'application/json'
          }
      })
      let elemento = await res.json()

      let promArray = elemento.map( item => {        
          return [ item.countryregion]
      })
      
      let resultArray = await Promise.all(promArray)

      setUnArrayTodosLosPaises(resultArray)

      }
      catch(err) {
          console.log(err)
          window.alert(err)
      }
  }
    
    
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


    useEffect(() => {
        obtenerListaDePaisesXUsuario()
        obtenerListaDePaises()
        
    }, [count]); 

    //var classes = useStyles();

    return(
            <>
            <PageTitle title="Listas de paises a EDITAR, escoja la lista a editar" />            

            {unArray.map(elemento =>{ return [
              
            <Grid container spacing={4} >
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
                onClick={(e)=>updateLista(elemento.id, sec, flatenizarNombrePaises(elemento.paises)   ) }
                >    
                Cambiar Nombre
                </Button>
              </Grid>
              <Grid item xs={3}   justify="flex-end">
                <Button  variant="contained" color="secondary"
                onClick={()=>// REVISAR ESTO !!!!(listarray[listarray.length - 1])}
                updateLista(elemento.id, sec, flatenizarNombrePaises(listarray[listarray.length - 1])   )}
                >
                Actualizar paises de la lista
                </Button>
              </Grid>
              </Grid>
              </Grid> 
              <Grid item xs={4}  >
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
              </Grid><Grid item xs={4}  >
              <MUIDataTable
              title="FILTRADO"
              data={unArrayTodosLosPaises}
              columns={["Paises para agregar"]}
              options={{
                  filter: true,
                  selectableRows: 'multiple',
                  selectableRowsOnClick: true,
                  filterType: 'dropdown',
                  responsive: 'stacked',
                  rowsPerPage: 10,
                  rowsSelected: state.rowsSelected,
                  onRowsSelect:  (rowsSelected, allRows) => { //console.log(unArrayTodosLosPaises[rowsSelected.dataIndex])
                  listarray.push(allRows.map( item => unArrayTodosLosPaises[item.dataIndex]) );
                  //setPaisElegido(true)
                },
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
  return [].concat.apply([], elemento);
}
