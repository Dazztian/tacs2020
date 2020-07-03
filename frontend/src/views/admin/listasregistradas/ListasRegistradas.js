import React, {useState, useEffect} from 'react'
import { TextField ,Container } from '@material-ui/core';
import { Grid} from "@material-ui/core";
import Button from '@material-ui/core/Button';
import Api from "../../../apis/Api"




const ListasRegistradas = ()=>{
    const api = new Api()
    const [fechaInicio, setFechaInicio] =useState()
    const [fechaFin, setFechaFin] =useState()

    const obtenerListasPorFechas = async(fechaInicio, fechaFin) =>{
        try{            
            /*let res = await fetch( BASE_URL + "/api/admin/report/lists?startDate=" + fechaInicio + "&endDate=" + fechaFin,{ 
                method:"GET",
                headers:{
                    'Accept': 'application/json',
                    'Authorization' : 'Bearer '+ tokenAdmin
                }
            })*/
            const res = await api.getAmountOfListsFromTo(fechaInicio, fechaFin)
            let elemento = await res.json()
            window.alert("La cantidad de listas es: " +elemento.totalLists)
        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        obtenerListasPorFechas('1/15/2020','12/15/2020')
    }, 0);    


    

return(
    <>
    
        <Grid container xs={12} spacing={4}> 
        <Grid item xs={2}> 
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
            <Grid item xs={2}>
                <TextField
                id="date"
                label="Fecha final"
                type="date"
                defaultValue="2017-05-24"
                onChange={e => setFechaFin(reconvertirFormatoFecha(e.target.value))}
                InputLabelProps={{
                shrink: true,
                }}
                />
            </Grid>
            <Grid item xs={2}>
            <Button  variant="contained" color="secondary" 
            onClick={()=> !fechaInicio || !fechaFin ?
            window.alert("Debe elegir las fechas a comparar primero"):
            obtenerListasPorFechas(fechaInicio,fechaFin)}
            >
            Seleccion libre
            </Button>
            </Grid>
        </Grid>
        <Grid container xs={12} spacing={4}> 
            <Grid  item lg={12}  > Consultar cantidad de listas creadas desde:</Grid>
        </Grid>
        <Grid container xs={7} spacing={4}> 
            <Grid  item lg={12}  > 
            <Button variant="contained" color="primary"
            onClick={()=>obtenerListasPorFechas(obtenerFechaActual(),obtenerFechaActual())}>HOY</Button>
            </Grid>
            <Grid  item lg={12}  > 
            <Button  variant="contained" color="primary"
            onClick={()=>obtenerListasFechaUltimos3Dias()}>Ultimos 3 dias</Button>
            </Grid>
            <Grid  item xs> 
            <Button  variant="contained" color="primary" 
            onClick={()=>obtenerListasFechaUltimaSemana()}>Ultima SEMANA</Button>
            </Grid>
            <Grid  item lg={12}  > 
            <Button  variant="contained" color="primary" 
            onClick={()=>obtenerListasFechaUltimoMes()}>Ultimo MES</Button>
            </Grid>
            <Grid  item lg={12}  > 
            <Button  variant="contained" color="primary" 
            onClick={()=>obtenerListasFechaDesdeElInicio()}>El comienzo</Button>
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
    
    function obtenerFechaActual() {
        var today = new Date();
        var date = (today.getMonth()+1)+'/'+today.getDate() +'/'+ today.getFullYear();
        return date
    }

     function obtenerListasFechaUltimoMes() {
        obtenerListasFechaUltimosNDias(30)
    }
    
    function obtenerListasFechaUltimaSemana() {
        obtenerListasFechaUltimosNDias(7)
    }
    function obtenerListasFechaUltimos3Dias() {
        obtenerListasFechaUltimosNDias(3)
    }
    
    function obtenerListasFechaUltimosNDias(cantDias) {
        var date = new Date();
        var last = new Date(date.getTime() - ( cantDias * 24 * 60 * 60 * 1000));
        let diaInicial = last.getDate()
        let mesInicial = last.getMonth() +1
        let anioInicial = last.getFullYear()
        let fechaInicial= mesInicial +'/'+ diaInicial +'/'+ anioInicial;
        console.log(fechaInicial)
        obtenerListasPorFechas(fechaInicial, obtenerFechaActual() ) 
    }

    function obtenerListasFechaDesdeElInicio() {
        let fechaComienzo= '1/1/2020'
        obtenerListasPorFechas(fechaComienzo, obtenerFechaActual() )
    }

    //Te devuelve el dia del anio desde el dia  1 hasta 365
    function obtenerDia365DelAnio() {
        var now = new Date();
        var start = new Date(now.getFullYear(), 0, 0);
        var diff = (now - start) + ((start.getTimezoneOffset() - now.getTimezoneOffset()) * 60 * 1000);
        var oneDay = 1000 * 60 * 60 * 24;
        var day = Math.floor(diff / oneDay);
        return day
    }
}    

export default ListasRegistradas
