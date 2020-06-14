import React, {useState, useEffect} from 'react'
import {StyleSheet, Text, View, Modal} from "react-native-web";
import {TextInput, Button} from 'react-native-paper'
import { Grid, Container } from '@material-ui/core';

import MUIDataTable from "mui-datatables";
import ReactDOM from "react-dom";

import PageTitle from "../../../components/PageTitle/PageTitle";


const BorrarListas = ()=>{


    const [unArray,setUnArray] = useState([{name:null,paises:[]}])
    const [count,setCount] = useState(0)
    const [loading,setLoading] = useState(false)

    
    const id_user= "5ee3911bd0a2646d1b8f1279"
    const token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTM5MTFiZDBhMjY0NmQxYjhmMTI3OSIsImV4cCI6MTU5MjAwODEyM30.IOk4miqYmBhI6S3fgFSv4fRYvylUgj3Jo9a94s6_Pig"

    const obtenerListaDePaisesXUsuario = async ()=>{
        try{
            let res = await fetch("http://localhost:8080/api/user/"+ id_user + "/lists",{
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

            console.log(resultArray)

            setUnArray(resultArray)  
           

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }

    }

    //{{localPath}}/api/user/{{lastUserId}}/lists/5ed9051d66236a4280ec9603
    const borrarListaSeleccionada= async (lista)=>{
        try{
            let res = await fetch("http://localhost:8080/api/user/"+ id_user + "/lists/" + lista,{
            method:"DELETE",
            headers:{
                'Accept': 'application/json',
                'Authorization' : 'Bearer '+ token
                }
            })
            console.log("La lista: "+lista+ "Ha sido eliminada exitosamente")
        }
        catch(err) {
            console.log("ERROR AL ELIMINAR LA LISTA: "+ lista)
            console.log(err)
            window.alert(err)
        }

    }


    useEffect(() => {
        obtenerListaDePaisesXUsuario()
    }, [count]); 

    return(
            <>
            {console.log("Esto es DENTRO del RETURN: "),
            console.log(unArray.length)}

            <PageTitle title="Listas de paises a BORRAR, escoja la lista a BORRAR" />            

            {unArray.map(elemento =>{ return [
            <Grid container spacing={4}>
            <Grid item lg={10} md={4} sm={6} xs={12}>
            <MUIDataTable
            title={ elemento.name +'  '+ elemento.id}
            data={elemento.paises}
            columns={["paises"]}
            options={{
                filter: true,
                selectableRows: 'multiple',
                selectableRowsOnClick: true,
                filterType: 'dropdown',
                responsive: 'stacked',
                rowsPerPage: 10,
                onRowsDelete: ()=>{borrarListaSeleccionada(elemento.id)}
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


export default BorrarListas 