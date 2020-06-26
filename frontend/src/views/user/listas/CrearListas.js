import React, {useState, useEffect} from 'react'
import { TextField ,Container } from '@material-ui/core';

import MUIDataTable from "mui-datatables";
import ReactDOM from "react-dom";

import PageTitle from "../../../components/PageTitle/PageTitle";

const CrearListas = ()=>{

    const state = { rowsSelected: [] };

    var listarray = new Array();

    const [unArray,setUnArray] = useState([])
    const [loading,setLoading] = useState(false)

    const [nombreLista,setNombreLista] = useState("")

    const [paisElegido,setPaisElegido] = useState(false)

    const [count,setCount] = useState(0)
    const [paises, setPaises] = useState([])

    //esta funcion podria ir definida en el archivo api.js
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
            return [ item.countryregion, item._id]
        })
        
        let resultArray = await Promise.all(promArray)

        setUnArray(resultArray)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    const id_user= "5ee7832eccd6b51684d61dd0"
    const BASE_URL = 'http://localhost:8080';
    const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTc4MzJlY2NkNmI1MTY4NGQ2MWRkMCIsImV4cCI6MTU5MjI2NjcwMn0.kFiKa2zV5vHF18NG_khjR8xKpmpLKk6BMYFPcntI0_M"

    const crearListaDePaisesXUsuario = async ()=>{
        try{
            let res = await fetch( BASE_URL +"/api/user/"+ id_user + "/lists",{
            //let res = await fetch("https://32ddbafd6091.ngrok.io/api/user/"+ id_user + "/lists",{
            method:"post",
            headers:{
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : 'Bearer '+ token
                },
                body:JSON.stringify({
                    "name": nombreLista,
                    //"countries": ["Argentina, Estados Unidos, Italia"]
                    "countries":listarray[listarray.length - 1]
                    //"countries":paises
                })
            })
            window.alert("Lista: "+nombreLista+ " creada exitosamente")
        }
        catch(err) {
            console.log("ERROR AL CREAR LISTA")
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        setLoading(true)
        obtenerListaDePaises()
        setLoading(false)
    }, [count]);    



return(
    <>
        {loading} ? <div>loading...poner una ruedita girando</div> :
        <PageTitle title="Lista de paises, Escoja al menos uno para crear la lista" button="CREAR LISTA"
         buttonEvent={()=> paisElegido? crearListaDePaisesXUsuario(): window.alert("Primero debes elegir un pais  al menos")} />
        <TextField id="outlined-basic" label="Nombre de la lista" variant="outlined" 
         onChange={e => setNombreLista(e.target.value)}
        />
        <MUIDataTable
            title="FILTRADO"
            data={unArray}
            columns={["countryregion", "id"]}
            options={{
                filter: true,
                selectableRows: 'multiple',
                selectableRowsOnClick: true,
                filterType: 'dropdown',
                responsive: 'stacked',
                rowsPerPage: 10,
                rowsSelected: state.rowsSelected,
            onRowsSelect:  (rowsSelected, allRows) => {
            //["nombrePais, ....etc"]                
            listarray.push(allRows.map( item => unArray[item.dataIndex][0]) );
            setPaisElegido(true)
            //console.log(listarray)
            },
            }
            }
        />
    </>
    )

}    

export default CrearListas
