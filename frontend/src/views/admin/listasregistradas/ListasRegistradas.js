import React, {useState, useEffect} from 'react'
import { TextField ,Container } from '@material-ui/core';
import { Grid} from "@material-ui/core";
import Button from '@material-ui/core/Button';


const ListasRegistradas = ()=>{

    const [fechaInicio, setFechaInicio] =useState()
    const [fechaFin, setFechaFin] =useState()


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
                onChange={e => setFechaInicio(reconvertirFormatoFecha(e.target.value))}
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
                //onChange={e => console.log(reconvertirFormatoFecha(e.target.value))}
                onChange={e => setFechaFin(reconvertirFormatoFecha(e.target.value))}
                InputLabelProps={{
                shrink: true,
                }}
                />
            </Grid>
            <Button  variant="contained" color="secondary" 
            onClick={()=> !fechaInicio || !fechaFin ?
            window.alert("Debe elegir las fechas a comparar primero"):
            //obtenerListasPorFechas('1/15/2020','12/15/2020')}
            obtenerListasPorFechas(fechaInicio,fechaFin)}
            >
            Obtener Cant Listas
            </Button>
        </Grid>
    </Grid>
    </>
    )
    //0123456789       
    //2017-05-29 a 1/15/2020
    function reconvertirFormatoFecha(unaFecha){
        let dia= unaFecha.substring(8, 10);
        let mes= unaFecha.substring(5, 7);
        let anio= unaFecha.substring(0, 4);

        let fecha= mes + '/' + dia + '/' + anio
        return fecha
       }

}    

export default ListasRegistradas
