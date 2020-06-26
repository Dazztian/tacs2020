import React, {useState, useEffect} from 'react'

import MUIDataTable from "mui-datatables";


import PageTitle from "../../../components/PageTitle/PageTitle";

const CantUsuariosInteresados = ()=>{

    const state = { rowsSelected: [] };

    const [unArray,setUnArray] = useState([])
    const [CantUsuariosInteresados, setCantUsuariosInteresados]=useState({})
    const [loading,setLoading] = useState(false)

    const [count,setCount] = useState(0)

    const BASE_URL = 'https://dcd471a082b5.ngrok.io';
    const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTQwODJkMDMwNzcyNjA5Y2IzOWQ2ZiIsImV4cCI6MTU5MzIxODU4OH0.zEd2mnPtfovp65fFPkv3WPKSY1RPPmMq5xrpleGv0F8"

    const obtenerListaDePaises = async ()=>{
        try{
        let res = await fetch(BASE_URL+"/api/countries/names",{
            method:"GET",
            headers:{
                'Accept': 'application/json'
            }
        })
        let elemento = await res.json()

        let promArray = elemento.map( item => {  return [ item.name, item.iso2] })
        
        let resultArray = await Promise.all(promArray)

        setUnArray(resultArray)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    const obtenerCantUsuariosInteresados= async (nombreDelPais, paisIsoCode) =>{
        try{
            let res = await fetch(BASE_URL+"/api/admin/report/"+paisIsoCode+"/list",{
            method:"GET",
            headers:{
                'Accept': 'application/json',
                'Authorization' : 'Bearer '+ token
            }
        })
        let elemento = await res.json()

        let promArray = elemento.totalUsers

        window.alert("Cant de usuarios interesados en "+nombreDelPais+ ": " + promArray)
        console.log(promArray)
        
        }
        catch(err) {
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
        <PageTitle title="Escoja al menos un pais para obtener la cant de usuarios interesados" />
        <MUIDataTable
            title="Lista de paises"
            data={unArray}
            columns={["Nombre", "iso2"]}
            options={{
                filter: true,
                selectableRows: 'multiple',
                selectableRowsOnClick: true,
                filterType: 'dropdown',
                responsive: 'stacked',
                rowsPerPage: 10,
                rowsSelected: state.rowsSelected,
            onRowsSelect:  (rowsSelected, allRows) => {
            allRows.map( item => obtenerCantUsuariosInteresados(unArray[item.dataIndex][0],unArray[item.dataIndex][1]) );
            },
            }
            }
        />
    </>
    )

}    

export default CantUsuariosInteresados
