import React, {useState, useEffect} from 'react'
import { TextField ,Container } from '@material-ui/core';
import { Grid} from "@material-ui/core";
import Button from '@material-ui/core/Button';



const ListasRegistradas = ()=>{

    const BASE_URL = 'https://dcd471a082b5.ngrok.io';
    const tokenAdmin = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTQwODJkMDMwNzcyNjA5Y2IzOWQ2ZiIsImV4cCI6MTU5MzIxODU4OH0.zEd2mnPtfovp65fFPkv3WPKSY1RPPmMq5xrpleGv0F8"

    // obtenerListasPorFechas('1/15/2020','12/15/2020')
    const obtenerListasPorFechas = async(fechaInicio, fechaFin) =>{
        try{
            let res = await fetch( BASE_URL + "/api/admin/report/lists?startDate=" + fechaInicio + "&endDate=" + fechaFin,{
                method:"GET",
                headers:{
                    'Accept': 'application/json',
                    'Authorization' : 'Bearer '+ tokenAdmin
                }
            })
            let elemento = await res.json()
            console.log(elemento.totalLists)
        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        //MM/DD/YYYY
        obtenerListasPorFechas('1/15/2020','12/15/2020')
    }, 0);    


return(
    <>
    <Grid container spacing={4}>
        <Grid container lg={12}> 
            <Grid container xs={6}> 
                <TextField
                id="date"
                label="Fecha inicio"
                type="date"
                defaultValue="2017-05-24"
                InputLabelProps={{
                shrink: true,
                }}
                />
                </Grid>
            <Grid container lg={12}> 
                <TextField
                id="date"
                label="Fecha final"
                type="date"
                defaultValue="2017-05-24"
                InputLabelProps={{
                shrink: true,
                }}
                />
            </Grid>
            <Button  variant="contained" color="secondary" 
            onClick={()=> //elObjetoEstaVacio(ListasUsuarioParticular) || elObjetoEstaVacio(ListasUsuarioParticular2) ?
            //window.alert("Debe elegir las a comparar listas primero"):
            obtenerListasPorFechas('1/15/2020','12/15/2020')}
            >
            Obtener Cant Listas
            </Button>
        </Grid>
    </Grid>
    </>
    )

}    

export default ListasRegistradas
